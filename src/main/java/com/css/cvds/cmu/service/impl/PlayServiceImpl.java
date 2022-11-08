package com.css.cvds.cmu.service.impl;

import java.text.ParseException;

import javax.sip.InvalidArgumentException;
import javax.sip.ResponseEvent;
import javax.sip.SipException;

import com.css.cvds.cmu.conf.MediaConfig;
import com.css.cvds.cmu.conf.SipConfig;
import com.css.cvds.cmu.conf.UserSetting;
import com.css.cvds.cmu.conf.exception.SsrcTransactionNotFoundException;
import com.css.cvds.cmu.gb28181.bean.*;
import com.css.cvds.cmu.gb28181.event.SipSubscribe;
import com.css.cvds.cmu.gb28181.session.SsrcConfig;
import com.css.cvds.cmu.gb28181.session.VideoStreamSessionManager;
import com.css.cvds.cmu.gb28181.transmit.cmd.impl.SIPCommander;
import com.css.cvds.cmu.media.css.MediaServerItem;
import com.css.cvds.cmu.service.ILogService;
import com.css.cvds.cmu.storager.IRedisCatchStorage;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import com.css.cvds.cmu.utils.SysLogEnum;
import com.css.cvds.cmu.utils.UserLogEnum;
import com.css.cvds.cmu.web.bean.ErrorCode;
import com.css.cvds.cmu.web.bean.WVPResult;
import com.css.cvds.cmu.service.IDeviceService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import com.alibaba.fastjson.JSONObject;
import com.css.cvds.cmu.common.StreamInfo;
import com.css.cvds.cmu.service.IMediaServerService;
import com.css.cvds.cmu.service.IPlayService;

@SuppressWarnings(value = {"rawtypes", "unchecked"})
@Service
public class PlayServiceImpl implements IPlayService {

    private final static Logger logger = LoggerFactory.getLogger(PlayServiceImpl.class);

    @Autowired
    private SipConfig sipConfig;

    @Autowired
    private SIPCommander cmder;

    @Autowired
    private IRedisCatchStorage redisCatchStorage;

    @Autowired
    private IVideoManagerStorage storager;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private VideoStreamSessionManager streamSession;

    @Autowired
    private MediaConfig mediaConfig;

    @Autowired
    private UserSetting userSetting;

    @Autowired
    private ILogService logService;

    @Override
    public void play(MediaServerItem mediaServerItem, Device device, String channelId,
                     SipSubscribe.Event okEvent, SipSubscribe.Event errorEvent) {

        logger.info("[点播开始] deviceId: {}, channelId: {},收流端口： {}, 收流模式：{}, SSRC: {}, SSRC校验：{}", device.getDeviceId(), channelId, mediaServerItem.getPort(), device.getStreamMode(), mediaServerItem.getSsrc(), device.isSsrcCheck() );
        final String ssrc = mediaServerItem.getSsrc();
        //端口获取失败的ssrcInfo 没有必要发送点播指令
        if(mediaServerItem.getPort() <= 0) {
            logger.info("[点播端口分配异常]，deviceId={},channelId={},ssrc={}", device.getDeviceId(), channelId, ssrc);
            return;
        }
        try {
            cmder.playStreamCmd(mediaServerItem, device, channelId, (okEvt) -> {
//                ResponseEvent responseEvent = (ResponseEvent)okEvt.event;
//                String contentString = new String(responseEvent.getResponse().getRawContent());
//                // 获取ssrc
//                int ssrcIndex = contentString.indexOf("y=");
//                // 检查是否有y字段
//                if (ssrcIndex >= 0) {
//                    //ssrc规定长度为10字节，不取余下长度以避免后续还有“f=”字段 TODO 后续对不规范的非10位ssrc兼容
//                    String ssrcInResponse = contentString.substring(ssrcIndex + 2, ssrcIndex + 12);
//                    // 查询到ssrc不一致且开启了ssrc校验则需要针对处理
//                    if (ssrc.equals(ssrcInResponse)) {
//                        return;
//                    }
//                    logger.info("[点播消息] 收到invite 200, 发现下级自定义了ssrc: {}", ssrcInResponse );
//                    if (!mediaServerItem.isRtpEnable() || device.isSsrcCheck()) {
//                        logger.info("[点播消息] SSRC修正 {}->{}", ssrc, ssrcInResponse);
//
//                        if (!mediaServerItem.getSsrcConfig().checkSsrc(ssrcInResponse)) {
//                            // ssrc 不可用
//                            // 释放ssrc
//                            mediaServerService.releaseSsrc(mediaServerItem.getId(), mediaServerItem.getSsrc());
//                            streamSession.remove(device.getDeviceId(), channelId, mediaServerItem.getStream());
//                            okEvt.msg = "下级自定义了ssrc,但是此ssrc不可用";
//                            okEvt.statusCode = 400;
//                            errorEvent.response(okEvt);
//                        }
//                    }
//                }

                StreamInfo streamInfo = new StreamInfo();
                streamInfo.setDeviceId(device.getDeviceId());
                streamInfo.setChannelId(channelId);
                streamInfo.setStream(mediaServerItem.getStream());
                streamInfo.setIp(mediaServerItem.getIp());
                redisCatchStorage.startPlay(streamInfo);
                storager.startPlay(device.getDeviceId(), channelId, mediaServerItem.getStream());

                okEvent.response(okEvt);
            }, (event) -> {
                // 释放ssrc
                mediaServerService.releaseSsrc(mediaServerItem.getId(), mediaServerItem.getSsrc());
                streamSession.remove(device.getDeviceId(), channelId, mediaServerItem.getStream());

                errorEvent.response(event);
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {
            logger.error("[命令发送失败] 点播消息: {}", e.getMessage());
            // 释放ssrc
            mediaServerService.releaseSsrc(mediaServerItem.getId(), mediaServerItem.getSsrc());

            streamSession.remove(device.getDeviceId(), channelId, mediaServerItem.getStream());
            SipSubscribe.EventResult eventResult = new SipSubscribe.EventResult(new CmdSendFailEvent(null));
            eventResult.msg = "命令发送失败";
            errorEvent.response(eventResult);
        }
    }

    @Override
    public DeferredResult<WVPResult<JSONObject>> play(String deviceId, String channelId, String ip, Integer port) {
        DeferredResult<WVPResult<JSONObject>> resultDeferredResult =
                new DeferredResult<>(userSetting.getPlayTimeout().longValue() + 10);

        resultDeferredResult.onTimeout(()->{
            logService.addSysLog(SysLogEnum.STREAM, "启动推流超时：" + deviceId);
            logger.info("等待超时");
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "超时"));
        });

        Device device = deviceService.queryDevice(deviceId);
        if (device == null ) {
            logService.addSysLog(SysLogEnum.STREAM, "启动推流失败：" + deviceId);
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "device[ " + deviceId + " ]未找到"));
            return resultDeferredResult;
        } else if (device.getOnline() == 0) {
            logService.addSysLog(SysLogEnum.STREAM, "启动推流失败（设备不在线）：" + deviceId);
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "device[ " + channelId + " ]offline"));
            return resultDeferredResult;
        }

        DeviceChannel deviceChannel = storager.queryChannel(deviceId, channelId);
        if (deviceChannel == null) {
            logService.addSysLog(SysLogEnum.STREAM, "启动推流失败（未找到通道）：" + deviceId);
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR400.getCode(), "channel[ " + channelId + " ]未找到"));
            return resultDeferredResult;
        } else if (deviceChannel.getStatus() == 0) {
            logService.addSysLog(SysLogEnum.STREAM, "启动推流失败（通道不在线）：" + deviceId);
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "channel[ " + channelId + " ]offline"));
            return resultDeferredResult;
        }

        MediaServerItem mediaServerItem = mediaConfig.getMediaSerItem();
        mediaServerItem.setSsrcConfig(new SsrcConfig(null, sipConfig.getDomain()));
        mediaServerItem.setSsrc(mediaServerItem.getSsrcConfig().getPlaySsrc());
        mediaServerItem.setPort(port);

        String streamId;
        if (mediaServerItem.isRtpEnable()) {
            streamId = String.format("%s_%s", device.getDeviceId(), channelId);
        } else {
            streamId = String.format("%s_%s_nonRtp", device.getDeviceId(), channelId);
        }
        if (StringUtils.isNoneBlank(ip)) {
            mediaServerItem.setIp(ip);
            mediaServerItem.setSdpIp(ip);
        }
        mediaServerItem.setStream(streamId);

        play(mediaServerItem, device, channelId, (okEvent) -> {
            ResponseEvent responseEvent = (ResponseEvent)okEvent.event;
            String contentString = new String(responseEvent.getResponse().getRawContent());

            JSONObject result = new JSONObject();
            // 获取ssrc
            final String videoSign = "m=video";
            int videoIndex = contentString.indexOf(videoSign);
            if (videoIndex >= 0) {
                String contentVideo = contentString.substring(videoIndex + videoSign.length());
                contentVideo = contentVideo.replaceAll("^[　 ]+", "");
                int portEndIndex = contentVideo.indexOf(' ');
                if (portEndIndex > 0) {
                    result.put("port", contentVideo.substring(0, portEndIndex));
                } else {
                    result.put("port", contentVideo);
                }
            }
            int ssrcIndex = contentString.indexOf("y=");
            // 检查是否有y字段
            if (ssrcIndex >= 0) {
                //ssrc规定长度为10字节，不取余下长度以避免后续还有“f=”字段 TODO 后续对不规范的非10位ssrc兼容
                String ssrcInResponse = contentString.substring(ssrcIndex + 2, ssrcIndex + 12);
                result.put("ssrc", ssrcInResponse);
            }
            result.put("sdp", contentString);
            resultDeferredResult.setResult(WVPResult.success(result));
            logService.addSysLog(SysLogEnum.STREAM, "启动推流成功：" + deviceId);
        }, (eventResult) -> {
            logService.addSysLog(SysLogEnum.STREAM, "启动推流失败：" + deviceId);
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(),
                    "channel[ " + channelId + " ] " + eventResult.msg));
        });
        return resultDeferredResult;
    }

    @Override
    public WVPResult<String> stop(String deviceId, String channelId) {
        StreamInfo streamInfo = redisCatchStorage.queryPlayByDevice(deviceId, channelId);
        if (streamInfo == null) {
            return WVPResult.fail(ErrorCode.ERROR400.getCode(), "stream[ " + channelId + " ]未找到");
        }
        Device device = deviceService.queryDevice(deviceId);
        if (device == null) {
            return WVPResult.fail(ErrorCode.ERROR400.getCode(), "device[ " + channelId + " ]未找到设备");
        }
        try {
            cmder.streamByeCmd(device, channelId, streamInfo.getStream(), null);
        } catch (InvalidArgumentException | ParseException | SipException | SsrcTransactionNotFoundException e) {
            return WVPResult.fail(ErrorCode.ERROR100.getCode(), "发送BYE失败：" + e.getMessage());
        }
        logService.addSysLog(SysLogEnum.STREAM, "停止推流：" + deviceId);
        redisCatchStorage.stopPlay(streamInfo);
        storager.stopPlay(streamInfo.getDeviceId(), streamInfo.getChannelId());
        return WVPResult.success(null);
    }
}
