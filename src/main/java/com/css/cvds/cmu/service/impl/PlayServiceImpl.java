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

        logger.info("[????????????] deviceId: {}, channelId: {},??????????????? {}, ???????????????{}, SSRC: {}, SSRC?????????{}", device.getDeviceId(), channelId, mediaServerItem.getPort(), device.getStreamMode(), mediaServerItem.getSsrc(), device.isSsrcCheck() );
        final String ssrc = mediaServerItem.getSsrc();
        //?????????????????????ssrcInfo ??????????????????????????????
        if(mediaServerItem.getPort() <= 0) {
            logger.info("[????????????????????????]???deviceId={},channelId={},ssrc={}", device.getDeviceId(), channelId, ssrc);
            return;
        }
        try {
            cmder.playStreamCmd(mediaServerItem, device, channelId, (okEvt) -> {
//                ResponseEvent responseEvent = (ResponseEvent)okEvt.event;
//                String contentString = new String(responseEvent.getResponse().getRawContent());
//                // ??????ssrc
//                int ssrcIndex = contentString.indexOf("y=");
//                // ???????????????y??????
//                if (ssrcIndex >= 0) {
//                    //ssrc???????????????10???????????????????????????????????????????????????f=????????? TODO ????????????????????????10???ssrc??????
//                    String ssrcInResponse = contentString.substring(ssrcIndex + 2, ssrcIndex + 12);
//                    // ?????????ssrc?????????????????????ssrc???????????????????????????
//                    if (ssrc.equals(ssrcInResponse)) {
//                        return;
//                    }
//                    logger.info("[????????????] ??????invite 200, ????????????????????????ssrc: {}", ssrcInResponse );
//                    if (!mediaServerItem.isRtpEnable() || device.isSsrcCheck()) {
//                        logger.info("[????????????] SSRC?????? {}->{}", ssrc, ssrcInResponse);
//
//                        if (!mediaServerItem.getSsrcConfig().checkSsrc(ssrcInResponse)) {
//                            // ssrc ?????????
//                            // ??????ssrc
//                            mediaServerService.releaseSsrc(mediaServerItem.getId(), mediaServerItem.getSsrc());
//                            streamSession.remove(device.getDeviceId(), channelId, mediaServerItem.getStream());
//                            okEvt.msg = "??????????????????ssrc,?????????ssrc?????????";
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
                // ??????ssrc
                mediaServerService.releaseSsrc(mediaServerItem.getId(), mediaServerItem.getSsrc());
                streamSession.remove(device.getDeviceId(), channelId, mediaServerItem.getStream());

                errorEvent.response(event);
            });
        } catch (InvalidArgumentException | SipException | ParseException e) {
            logger.error("[??????????????????] ????????????: {}", e.getMessage());
            // ??????ssrc
            mediaServerService.releaseSsrc(mediaServerItem.getId(), mediaServerItem.getSsrc());

            streamSession.remove(device.getDeviceId(), channelId, mediaServerItem.getStream());
            SipSubscribe.EventResult eventResult = new SipSubscribe.EventResult(new CmdSendFailEvent(null));
            eventResult.msg = "??????????????????";
            errorEvent.response(eventResult);
        }
    }

    @Override
    public DeferredResult<WVPResult<JSONObject>> play(String deviceId, String channelId, String ip, Integer port) {
        DeferredResult<WVPResult<JSONObject>> resultDeferredResult =
                new DeferredResult<>(userSetting.getPlayTimeout().longValue() + 10);

        resultDeferredResult.onTimeout(()->{
            logService.addSysLog(SysLogEnum.STREAM, "?????????????????????" + deviceId);
            logger.info("????????????");
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "??????"));
        });

        Device device = deviceService.queryDevice(deviceId);
        if (device == null ) {
            logService.addSysLog(SysLogEnum.STREAM, "?????????????????????" + deviceId);
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "device[ " + deviceId + " ]?????????"));
            return resultDeferredResult;
        } else if (device.getOnline() == 0) {
            logService.addSysLog(SysLogEnum.STREAM, "??????????????????????????????????????????" + deviceId);
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "device[ " + channelId + " ]offline"));
            return resultDeferredResult;
        }

        DeviceChannel deviceChannel = storager.queryChannel(deviceId, channelId);
        if (deviceChannel == null) {
            logService.addSysLog(SysLogEnum.STREAM, "??????????????????????????????????????????" + deviceId);
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR400.getCode(), "channel[ " + channelId + " ]?????????"));
            return resultDeferredResult;
        } else if (deviceChannel.getStatus() == 0) {
            logService.addSysLog(SysLogEnum.STREAM, "??????????????????????????????????????????" + deviceId);
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
            // ??????ssrc
            final String videoSign = "m=video";
            int videoIndex = contentString.indexOf(videoSign);
            if (videoIndex >= 0) {
                String contentVideo = contentString.substring(videoIndex + videoSign.length());
                contentVideo = contentVideo.replaceAll("^[??? ]+", "");
                int portEndIndex = contentVideo.indexOf(' ');
                if (portEndIndex > 0) {
                    result.put("port", contentVideo.substring(0, portEndIndex));
                } else {
                    result.put("port", contentVideo);
                }
            }
            int ssrcIndex = contentString.indexOf("y=");
            // ???????????????y??????
            if (ssrcIndex >= 0) {
                //ssrc???????????????10???????????????????????????????????????????????????f=????????? TODO ????????????????????????10???ssrc??????
                String ssrcInResponse = contentString.substring(ssrcIndex + 2, ssrcIndex + 12);
                result.put("ssrc", ssrcInResponse);
            }
            result.put("sdp", contentString);
            resultDeferredResult.setResult(WVPResult.success(result));
            logService.addSysLog(SysLogEnum.STREAM, "?????????????????????" + deviceId);
        }, (eventResult) -> {
            logService.addSysLog(SysLogEnum.STREAM, "?????????????????????" + deviceId);
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(),
                    "channel[ " + channelId + " ] " + eventResult.msg));
        });
        return resultDeferredResult;
    }

    @Override
    public WVPResult<String> stop(String deviceId, String channelId) {
        StreamInfo streamInfo = redisCatchStorage.queryPlayByDevice(deviceId, channelId);
        if (streamInfo == null) {
            return WVPResult.fail(ErrorCode.ERROR400.getCode(), "stream[ " + channelId + " ]?????????");
        }
        Device device = deviceService.queryDevice(deviceId);
        if (device == null) {
            return WVPResult.fail(ErrorCode.ERROR400.getCode(), "device[ " + channelId + " ]???????????????");
        }
        try {
            cmder.streamByeCmd(device, channelId, streamInfo.getStream(), null);
        } catch (InvalidArgumentException | ParseException | SipException | SsrcTransactionNotFoundException e) {
            return WVPResult.fail(ErrorCode.ERROR100.getCode(), "??????BYE?????????" + e.getMessage());
        }
        logService.addSysLog(SysLogEnum.STREAM, "???????????????" + deviceId);
        redisCatchStorage.stopPlay(streamInfo);
        storager.stopPlay(streamInfo.getDeviceId(), streamInfo.getChannelId());
        return WVPResult.success(null);
    }
}
