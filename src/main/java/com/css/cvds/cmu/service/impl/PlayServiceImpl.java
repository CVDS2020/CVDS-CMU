package com.css.cvds.cmu.service.impl;

import java.text.ParseException;
import java.util.*;

import javax.sip.InvalidArgumentException;
import javax.sip.ResponseEvent;
import javax.sip.SipException;

import com.css.cvds.cmu.conf.DynamicTask;
import com.css.cvds.cmu.conf.MediaConfig;
import com.css.cvds.cmu.conf.SipConfig;
import com.css.cvds.cmu.conf.UserSetting;
import com.css.cvds.cmu.conf.exception.ControllerException;
import com.css.cvds.cmu.conf.exception.SsrcTransactionNotFoundException;
import com.css.cvds.cmu.gb28181.bean.*;
import com.css.cvds.cmu.gb28181.event.SipSubscribe;
import com.css.cvds.cmu.gb28181.session.SsrcConfig;
import com.css.cvds.cmu.gb28181.session.VideoStreamSessionManager;
import com.css.cvds.cmu.gb28181.transmit.cmd.impl.SIPCommander;
import com.css.cvds.cmu.gb28181.transmit.cmd.impl.SIPCommanderFroPlatform;
import com.css.cvds.cmu.media.css.MediaServerItem;
import com.css.cvds.cmu.service.bean.PlayResult;
import com.css.cvds.cmu.storager.IRedisCatchStorage;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import com.css.cvds.cmu.vmanager.bean.ErrorCode;
import com.css.cvds.cmu.vmanager.bean.WVPResult;
import com.css.cvds.cmu.service.IDeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import com.alibaba.fastjson.JSONObject;
import com.css.cvds.cmu.common.StreamInfo;
import com.css.cvds.cmu.gb28181.transmit.callback.DeferredResultHolder;
import com.css.cvds.cmu.gb28181.transmit.callback.RequestMessage;
import com.css.cvds.cmu.service.IMediaServerService;
import com.css.cvds.cmu.service.IPlayService;
import com.css.cvds.cmu.service.bean.InviteTimeOutCallback;
import com.css.cvds.cmu.service.bean.SSRCInfo;

@SuppressWarnings(value = {"rawtypes", "unchecked"})
@Service
public class PlayServiceImpl implements IPlayService {

    private final static Logger logger = LoggerFactory.getLogger(PlayServiceImpl.class);

    @Autowired
    private IVideoManagerStorage storager;

    @Autowired
    private SipConfig sipConfig;

    @Autowired
    private SIPCommander cmder;

    @Autowired
    private IRedisCatchStorage redisCatchStorage;

    @Autowired
    private DeferredResultHolder resultHolder;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private VideoStreamSessionManager streamSession;

    @Autowired
    private MediaConfig mediaConfig;

    @Autowired
    private UserSetting userSetting;

    @Autowired
    private DynamicTask dynamicTask;

    @Qualifier("taskExecutor")
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void play(MediaServerItem mediaServerItem, Device device, String channelId,
                     SipSubscribe.Event errorEvent,
                     InviteTimeOutCallback timeoutCallback, String uuid) {

        logger.info("[点播开始] deviceId: {}, channelId: {},收流端口： {}, 收流模式：{}, SSRC: {}, SSRC校验：{}", device.getDeviceId(), channelId, mediaServerItem.getPort(), device.getStreamMode(), mediaServerItem.getSsrc(), device.isSsrcCheck() );
        // 超时处理
        String timeOutTaskKey = UUID.randomUUID().toString();
        System.out.println("设置超时任务： " + timeOutTaskKey);
        dynamicTask.startDelay( timeOutTaskKey,()->{

            logger.info("[点播超时] 收流超时 deviceId: {}, channelId: {}，端口：{}, SSRC: {}", device.getDeviceId(), channelId, mediaServerItem.getPort(), mediaServerItem.getSsrc());
            timeoutCallback.run(1, "收流超时");
            // 点播超时回复BYE 同时释放ssrc以及此次点播的资源
            try {
                cmder.streamByeCmd(device, channelId, mediaServerItem.getStream(), null);
            } catch (InvalidArgumentException | ParseException | SipException e) {
                logger.error("[点播超时]， 发送BYE失败 {}", e.getMessage());
            } catch (SsrcTransactionNotFoundException e) {
                timeoutCallback.run(0, "点播超时");
                mediaServerService.releaseSsrc(mediaServerItem.getId(), mediaServerItem.getSsrc());
                streamSession.remove(device.getDeviceId(), channelId, mediaServerItem.getStream());
            }
        }, userSetting.getPlayTimeout());
        final String ssrc = mediaServerItem.getSsrc();
        //端口获取失败的ssrcInfo 没有必要发送点播指令
        if(mediaServerItem.getPort() <= 0){
            logger.info("[点播端口分配异常]，deviceId={},channelId={},ssrc={}", device.getDeviceId(), channelId, ssrc);
            return;
        }
        try {
            cmder.playStreamCmd(mediaServerItem, device, channelId, (event) -> {
                ResponseEvent responseEvent = (ResponseEvent)event.event;
                String contentString = new String(responseEvent.getResponse().getRawContent());
                // 获取ssrc
                int ssrcIndex = contentString.indexOf("y=");
                // 检查是否有y字段
                if (ssrcIndex >= 0) {
                    //ssrc规定长度为10字节，不取余下长度以避免后续还有“f=”字段 TODO 后续对不规范的非10位ssrc兼容
                    String ssrcInResponse = contentString.substring(ssrcIndex + 2, ssrcIndex + 12);
                    // 查询到ssrc不一致且开启了ssrc校验则需要针对处理
                    if (ssrc.equals(ssrcInResponse)) {
                        return;
                    }
                    logger.info("[点播消息] 收到invite 200, 发现下级自定义了ssrc: {}", ssrcInResponse );
                    if (!mediaServerItem.isRtpEnable() || device.isSsrcCheck()) {
                        logger.info("[点播消息] SSRC修正 {}->{}", ssrc, ssrcInResponse);

                        if (!mediaServerItem.getSsrcConfig().checkSsrc(ssrcInResponse)) {
                            // ssrc 不可用
                            // 释放ssrc
                            mediaServerService.releaseSsrc(mediaServerItem.getId(), mediaServerItem.getSsrc());
                            streamSession.remove(device.getDeviceId(), channelId, mediaServerItem.getStream());
                            event.msg = "下级自定义了ssrc,但是此ssrc不可用";
                            event.statusCode = 400;
                            errorEvent.response(event);
                            return;
                        }
                    }
                }
            }, (event) -> {
                dynamicTask.stop(timeOutTaskKey);
                // 释放ssrc
                mediaServerService.releaseSsrc(mediaServerItem.getId(), mediaServerItem.getSsrc());
                streamSession.remove(device.getDeviceId(), channelId, mediaServerItem.getStream());
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {

            logger.error("[命令发送失败] 点播消息: {}", e.getMessage());
            dynamicTask.stop(timeOutTaskKey);
            // 释放ssrc
            mediaServerService.releaseSsrc(mediaServerItem.getId(), mediaServerItem.getSsrc());

            streamSession.remove(device.getDeviceId(), channelId, mediaServerItem.getStream());
            SipSubscribe.EventResult eventResult = new SipSubscribe.EventResult(new CmdSendFailEvent(null));
            eventResult.msg = "命令发送失败";
            errorEvent.response(eventResult);
        }
    }

    @Override
    public PlayResult play(MediaServerItem mediaServerItem, String deviceId, String channelId,
                           SipSubscribe.Event errorEvent,
                           Runnable timeoutCallback) {
        if (Objects.isNull(mediaServerItem)) {
            throw new ControllerException(ErrorCode.ERROR100);
        }
        PlayResult playResult = new PlayResult();
        RequestMessage msg = new RequestMessage();
        String key = DeferredResultHolder.CALLBACK_CMD_PLAY + deviceId + channelId;
        msg.setKey(key);
        String uuid = UUID.randomUUID().toString();
        msg.setId(uuid);
        playResult.setUuid(uuid);
        DeferredResult<WVPResult<String>> result = new DeferredResult<>(userSetting.getPlayTimeout().longValue());
        playResult.setResult(result);
        // 录像查询以channelId作为deviceId查询
        resultHolder.put(key, uuid, result);

        Device device = redisCatchStorage.getDevice(deviceId);
        StreamInfo streamInfo = redisCatchStorage.queryPlayByDevice(deviceId, channelId);
        playResult.setDevice(device);

        if (streamInfo != null) {
            String streamId = streamInfo.getStream();
            if (streamId == null) {
                WVPResult wvpResult = new WVPResult();
                wvpResult.setCode(ErrorCode.ERROR100.getCode());
                wvpResult.setMsg("点播失败， redis缓存streamId等于null");
                msg.setData(wvpResult);
                resultHolder.invokeAllResult(msg);
                return playResult;
            }
        }
        if (streamInfo == null) {
            String streamId = null;
            if (mediaServerItem.isRtpEnable()) {
                streamId = String.format("%s_%s", device.getDeviceId(), channelId);
            } else {
                streamId = String.format("%s_%s_nonRtp", device.getDeviceId(), channelId);
            }
            SsrcConfig ssrcConfig = mediaServerItem.getSsrcConfig();
            SSRCInfo ssrcInfo = new SSRCInfo(mediaServerItem.getPort(), ssrcConfig.getPlaySsrc(), streamId);
            play(mediaServerItem, device, channelId, event -> {

            }, (code, msgStr)->{
                // invite点播超时
                WVPResult wvpResult = new WVPResult();
                wvpResult.setCode(ErrorCode.ERROR100.getCode());
                if (code == 0) {
                    wvpResult.setMsg("点播超时，请稍候重试");
                }else if (code == 1) {
                    wvpResult.setMsg("收流超时，请稍候重试");
                }
                msg.setData(wvpResult);
                // 回复之前所有的点播请求
                resultHolder.invokeAllResult(msg);
            }, uuid);
        }
        return playResult;
    }

    @Override
    public MediaServerItem getMediaServerItem(Device device) {
        if (device == null) {
            return null;
        }
        String mediaServerId = device.getMediaServerId();
        MediaServerItem mediaServerItem;
        if (mediaServerId == null) {
            mediaServerItem = mediaServerService.getMediaServerForMinimumLoad();
        } else {
            mediaServerItem = mediaServerService.getOne(mediaServerId);
        }
        return mediaServerItem;
    }

    @Override
    public MediaServerItem newMediaServerItem(Integer port) {
        MediaServerItem mediaServerItem = mediaConfig.getMediaSerItem();
        mediaServerItem.setSsrcConfig(new SsrcConfig(mediaServerItem.getId(), null, sipConfig.getDomain()));
        mediaServerItem.setPort(port);
        return mediaServerItem;
    }
}
