package com.css.cvds.cmu.web.gb28181;

import com.alibaba.fastjson.JSONObject;
import com.css.cvds.cmu.common.StreamInfo;
import com.css.cvds.cmu.conf.SipConfig;
import com.css.cvds.cmu.conf.UserSetting;
import com.css.cvds.cmu.conf.exception.SsrcTransactionNotFoundException;
import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.gb28181.bean.DeviceChannel;
import com.css.cvds.cmu.gb28181.session.SsrcConfig;
import com.css.cvds.cmu.gb28181.transmit.cmd.impl.SIPCommander;
import com.css.cvds.cmu.media.css.MediaServerItem;
import com.css.cvds.cmu.service.IDeviceService;
import com.css.cvds.cmu.service.IPlayService;
import com.css.cvds.cmu.service.bean.PlayResult;
import com.css.cvds.cmu.storager.IRedisCatchStorage;
import com.css.cvds.cmu.storager.IVideoManagerStorage;
import com.css.cvds.cmu.vmanager.bean.ErrorCode;
import com.css.cvds.cmu.vmanager.bean.WVPResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.sip.InvalidArgumentException;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import java.text.ParseException;
import java.util.Objects;

/**
 * API兼容：实时直播
 */
@SuppressWarnings(value = {"rawtypes", "unchecked"})
@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/stream")
public class ApiStreamController {

    private final static Logger logger = LoggerFactory.getLogger(ApiStreamController.class);

    @Autowired
    private SIPCommander cmder;

    @Autowired
    private IVideoManagerStorage storager;

    @Autowired
    private UserSetting userSetting;

    @Autowired
    private IRedisCatchStorage redisCatchStorage;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IPlayService playService;

    /**
     * 实时推流 - 开始推流
     * @param port 端口
     * @param channelId 通道编号
     * @return
     */
    @RequestMapping(value = "/start")
    private DeferredResult<WVPResult<JSONObject>> start(@RequestParam()String deviceId,
                                                        @RequestParam()String channelId,
                                                        @RequestParam(required = false)String ip,
                                                        @RequestParam()Integer port
    ) {
        DeferredResult<WVPResult<JSONObject>> resultDeferredResult = new DeferredResult<>(userSetting.getPlayTimeout().longValue() + 10);

        resultDeferredResult.onTimeout(()->{
            logger.info("等待超时");
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "超时"));
        });

        Device device = storager.queryVideoDevice(deviceId);
        if (device == null ) {
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "device[ " + deviceId + " ]未找到"));
            return resultDeferredResult;
        } else if (device.getOnline() == 0) {
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "device[ " + channelId + " ]offline"));
            return resultDeferredResult;
        }

        DeviceChannel deviceChannel = storager.queryChannel(deviceId, channelId);
        if (deviceChannel == null) {
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR400.getCode(), "channel[ " + channelId + " ]未找到"));
            return resultDeferredResult;
        } else if (deviceChannel.getStatus() == 0) {
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "channel[ " + channelId + " ]offline"));
            return resultDeferredResult;
        }

        String streamId;
        MediaServerItem mediaServerItem = playService.getMediaServerItem(device);
        if (Objects.isNull(mediaServerItem)) {
            mediaServerItem = playService.newMediaServerItem(10001);
            if (mediaServerItem.isRtpEnable()) {
                streamId = String.format("%s_%s", device.getDeviceId(), channelId);
            } else {
                streamId = String.format("%s_%s_nonRtp", device.getDeviceId(), channelId);
            }
            if (StringUtils.isNoneBlank(ip)) {
                mediaServerItem.setIp(ip);
                mediaServerItem.setSdpIp(ip);
            }
            mediaServerItem.setPort(port);
            mediaServerItem.setStream(streamId);
            mediaServerItem.setSsrc(mediaServerItem.getSsrcConfig().getPlaySsrc());
        } else {
            streamId = mediaServerItem.getStream();
        }
        playService.play(mediaServerItem, device, channelId, (okEvent) -> {
            StreamInfo streamInfo = new StreamInfo();
            streamInfo.setDeviceId(deviceId);
            streamInfo.setChannelId(channelId);
            streamInfo.setStream(streamId);
            streamInfo.setIp(ip);
            redisCatchStorage.startPlay(streamInfo);
            storager.startPlay(deviceId, channelId, streamId);

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
            resultDeferredResult.setResult(WVPResult.success(result));
        }, (eventResult) -> {
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(),
                    "channel[ " + channelId + " ] " + eventResult.msg));
        });
        return resultDeferredResult;
    }

    /**
     * 实时推流 - 停止推流
     * @param deviceId 设备编号
     * @param channelId 通道国标编号
     * @return
     */
    @RequestMapping(value = "/stop")
    @ResponseBody
    private WVPResult<JSONObject> stop(@RequestParam()String deviceId, @RequestParam()String channelId) {
        Device device = deviceService.queryDevice(deviceId);
        if (device == null) {
            return WVPResult.fail(ErrorCode.ERROR400.getCode(), "device[ " + channelId + " ]未找到设备");
        }
        StreamInfo streamInfo = redisCatchStorage.queryPlayByDevice(deviceId, channelId);
        if (streamInfo == null) {
            return WVPResult.fail(ErrorCode.ERROR400.getCode(), "stream[ " + channelId + " ]未找到");
        }
        try {
            cmder.streamByeCmd(device, channelId, streamInfo.getStream(), null);
        } catch (InvalidArgumentException | ParseException | SipException | SsrcTransactionNotFoundException e) {
            return WVPResult.fail(ErrorCode.ERROR100.getCode(), "发送BYE失败：" + e.getMessage());
        }
        redisCatchStorage.stopPlay(streamInfo);
        storager.stopPlay(streamInfo.getDeviceId(), streamInfo.getChannelId());
        return WVPResult.success(null);
    }

    /**
     * 流保活
     * @param deviceId 设备编号
     * @param channelId 通道序号
     * @return
     */
    @RequestMapping(value = "/touch")
    @ResponseBody
    private JSONObject touch(@RequestParam()String deviceId,
                             @RequestParam()Integer channelId
    ) {
        return null;
    }
}
