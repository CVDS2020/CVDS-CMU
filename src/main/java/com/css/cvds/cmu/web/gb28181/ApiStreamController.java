package com.css.cvds.cmu.web.gb28181;

import com.alibaba.fastjson.JSONObject;
import com.css.cvds.cmu.common.StreamInfo;
import com.css.cvds.cmu.conf.MediaConfig;
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

    @Autowired
    private MediaConfig mediaConfig;

    @Autowired
    private SipConfig sipConfig;

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
        // 先停止，再启动
        playService.stop(deviceId, channelId);

        return playService.play(deviceId, channelId, ip, port);
    }

    /**
     * 实时推流 - 停止推流
     * @param deviceId 设备编号
     * @param channelId 通道国标编号
     * @return
     */
    @RequestMapping(value = "/stop")
    @ResponseBody
    private WVPResult<String> stop(@RequestParam()String deviceId, @RequestParam()String channelId) {
        return playService.stop(deviceId, channelId);
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
