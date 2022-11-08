package com.css.cvds.cmu.web.statistics;

import com.css.cvds.cmu.service.bean.Statistics;
import com.css.cvds.cmu.service.IDeviceService;
import com.css.cvds.cmu.web.bean.WVPResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@Tag(name  = "统计信息")
@CrossOrigin
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final static Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private IDeviceService deviceService;

    @GetMapping("/get")
    @Operation(summary = "获取统计信息")
    public DeferredResult<WVPResult<Statistics>> get() {
        DeferredResult<WVPResult<Statistics>> resultDeferredResult =
                new DeferredResult<>(30 * 1000L);

        Statistics statistics = new Statistics();
        statistics.setCameraOnlineNum(deviceService.getOnlineDeviceCount());
        statistics.setCameraNum(deviceService.getDeviceCount());

        resultDeferredResult.onTimeout(() -> {
            logger.info("等待超时");
            resultDeferredResult.setResult(WVPResult.success(statistics));
        });

        resultDeferredResult.setResult(WVPResult.success(statistics));

        return resultDeferredResult;
    }
}
