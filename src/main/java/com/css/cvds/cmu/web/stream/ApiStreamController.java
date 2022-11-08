package com.css.cvds.cmu.web.stream;

import com.alibaba.fastjson.JSONObject;
import com.css.cvds.cmu.service.IPlayService;
import com.css.cvds.cmu.utils.UserLogEnum;
import com.css.cvds.cmu.web.bean.WVPResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * API兼容：实时直播
 */
@Tag(name  = "实时流管理")
@SuppressWarnings(value = {"rawtypes", "unchecked"})
@CrossOrigin
@RestController
@RequestMapping(value = "/api/stream")
public class ApiStreamController {

    private final static Logger logger = LoggerFactory.getLogger(ApiStreamController.class);

    @Autowired
    private IPlayService playService;

    /**
     * 实时推流 - 开始推流
     * @param port 端口
     * @param channelId 通道编号
     * @return
     */
    @Operation(summary = "开始推流")
    @GetMapping(value = "/start")
    @Parameter(name = "deviceId", description = "设备国标ID", required = true)
    @Parameter(name = "channelId", description = "通道国标ID", required = true)
    @Parameter(name = "ip", description = "接收视频的ip", required = true)
    @Parameter(name = "port", description = "接收视频的端口", required = true)
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
    @Operation(summary = "停止推流")
    @DeleteMapping(value = "/stop")
    @Parameter(name = "deviceId", description = "设备国标ID", required = true)
    @Parameter(name = "channelId", description = "通道国标ID", required = true)
    private WVPResult<String> stop(@RequestParam()String deviceId, @RequestParam()String channelId) {
        return playService.stop(deviceId, channelId);
    }
}
