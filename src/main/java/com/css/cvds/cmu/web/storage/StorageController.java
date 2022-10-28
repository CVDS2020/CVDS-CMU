package com.css.cvds.cmu.web.storage;

import com.css.cvds.cmu.web.bean.Disk;
import com.css.cvds.cmu.web.bean.StorageConfig;
import com.css.cvds.cmu.web.bean.WVPResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@Tag(name  = "存储管理")
@CrossOrigin
@RestController
@RequestMapping("/api/storage")
public class StorageController {

    private final static Logger logger = LoggerFactory.getLogger(StorageController.class);

    @GetMapping("/disk/list")
    @Operation(summary = "获取磁盘列表")
    @Parameter(name = "diskNo", description = "磁盘号")
    @Parameter(name = "status", description = "状态：空 全部，0 正常，1 故障")
    @Parameter(name = "type", description = "类型：空 全部，0 本地，1 外挂")
    @Parameter(name = "page", description = "当前页", required = true)
    @Parameter(name = "count", description = "每页查询数量", required = true)
    public DeferredResult<WVPResult<Disk>> list(@RequestParam(required = false)Integer page,
                                                @RequestParam(required = false)Integer count,
                                                Integer diskNo, Integer status, Integer type) {
        DeferredResult<WVPResult<Disk>> resultDeferredResult =
                new DeferredResult<>(30 * 1000L);

        Disk disk = new Disk();

        resultDeferredResult.onTimeout(() -> {
            logger.info("等待超时");
        });

        resultDeferredResult.setResult(WVPResult.success(disk));

        return resultDeferredResult;
    }

    @GetMapping("/config")
    @Operation(summary = "获取存储配置")
    public WVPResult<StorageConfig> storageConfig() {

        StorageConfig config = new StorageConfig();

        return WVPResult.success(config);
    }

    @PutMapping("/config")
    @Operation(summary = "更新存储配置")
    public WVPResult<?> saveStorageConfig(@RequestBody StorageConfig config) {
        return WVPResult.success(null);
    }
}
