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
    private DeferredResult<WVPResult<String>> start(@RequestParam()String channelId,
                                                    @RequestParam(required = false)String ip,
                                                    @RequestParam()Integer port
    ) {
        DeferredResult<WVPResult<String>> resultDeferredResult = new DeferredResult<>(userSetting.getPlayTimeout().longValue() + 10);

        resultDeferredResult.onTimeout(()->{
            logger.info("等待超时");
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "超时"));
        });

        DeviceChannel deviceChannel = storager.queryChannelByChannelId(channelId);
        if (deviceChannel == null) {
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR400.getCode(), "channel[ " + channelId + " ]未找到"));
            return resultDeferredResult;
        } else if (deviceChannel.getStatus() == 0) {
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "channel[ " + channelId + " ]offline"));
            return resultDeferredResult;
        }
        String deviceId = deviceChannel.getDeviceId();
        Device device = storager.queryVideoDevice(deviceId);
        if (device == null ) {
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "device[ " + deviceId + " ]未找到"));
            return resultDeferredResult;
        } else if (device.getOnline() == 0) {
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(), "device[ " + channelId + " ]offline"));
            return resultDeferredResult;
        }
        MediaServerItem mediaServerItem = playService.getMediaServerItem(device);
        if (Objects.isNull(mediaServerItem)) {
            String streamId;
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
        }
        playService.play(mediaServerItem, device, channelId, (okEvent) -> {
            resultDeferredResult.setResult(WVPResult.success(null));
        }, (eventResult) -> {
            resultDeferredResult.setResult(WVPResult.fail(ErrorCode.ERROR100.getCode(),
                    "channel[ " + channelId + " ] " + eventResult.msg));
        });
        return resultDeferredResult;
    }

    /**
     * 实时推流 - 停止推流
     * @param serial 设备编号
     * @param channel 通道序号
     * @param code 通道国标编号
     * @param check_outputs
     * @return
     */
    @RequestMapping(value = "/stop")
    @ResponseBody
    private JSONObject stop(String serial ,
                            @RequestParam(required = false)Integer channel ,
                            @RequestParam(required = false)String code,
                            @RequestParam(required = false)String check_outputs

    ) {
        StreamInfo streamInfo = redisCatchStorage.queryPlayByDevice(serial, code);
        if (streamInfo == null) {
            JSONObject result = new JSONObject();
            result.put("error","未找到流信息");
            return result;
        }
        Device device = deviceService.queryDevice(serial);
        if (device == null) {
            JSONObject result = new JSONObject();
            result.put("error","未找到设备");
            return result;
        }
        try {
            cmder.streamByeCmd(device, code, streamInfo.getStream(), null);
        } catch (InvalidArgumentException | ParseException | SipException | SsrcTransactionNotFoundException e) {
            JSONObject result = new JSONObject();
            result.put("error","发送BYE失败：" + e.getMessage());
            return result;
        }
        redisCatchStorage.stopPlay(streamInfo);
        storager.stopPlay(streamInfo.getDeviceID(), streamInfo.getChannelId());
        return null;
    }

    /**
     * 实时直播 - 直播流保活
     * @param serial 设备编号
     * @param channel 通道序号
     * @param code 通道国标编号
     * @return
     */
    @RequestMapping(value = "/touch")
    @ResponseBody
    private JSONObject touch(String serial ,String t,
                             @RequestParam(required = false)Integer channel ,
                             @RequestParam(required = false)String code,
                             @RequestParam(required = false)String autorestart,
                             @RequestParam(required = false)String audio,
                             @RequestParam(required = false)String cdn
    ){
        return null;
    }
}
