package com.css.cvds.cmu.web.record;

import com.css.cvds.cmu.service.IRecordInfoServer;
import com.css.cvds.cmu.storager.dao.dto.RecordInfo;
import com.css.cvds.cmu.web.bean.DownloadInfo;
import com.css.cvds.cmu.web.bean.WVPResult;
import com.css.cvds.cmu.web.storage.StorageController;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@Tag(name  = "云端录像")
@CrossOrigin
@RestController
@RequestMapping("/api/record")
public class RecordController {

    private final static Logger logger = LoggerFactory.getLogger(StorageController.class);

    @Autowired
    private IRecordInfoServer recordInfoServer;

    @Operation(summary = "录像列表查询")
    @GetMapping(value = "/list")
    @Parameter(name = "deviceId", description = "设备编号", required = true)
    @Parameter(name = "channelId", description = "通道ID")
    @Parameter(name = "page", description = "当前页", required = true)
    @Parameter(name = "count", description = "每页查询数量", required = true)
    public WVPResult<PageInfo<RecordInfo>> list(@RequestParam(required = false)Integer page,
                                  @RequestParam(required = false)Integer count){

        PageInfo<RecordInfo> recordList = recordInfoServer.getRecordList(page - 1, page - 1 + count);
        return WVPResult.success(recordList);
    }

    @Operation(summary = "录像详情")
    @GetMapping(value = "/detail/{id}")
    @Parameter(name = "id", description = "录像id", required = true)
    public WVPResult<RecordInfo> detail(@PathVariable String id){
        return null;
    }

    @GetMapping("/download")
    @Operation(summary = "下载视频")
    @Parameter(name = "startTime", description = "开始时间", required = true)
    @Parameter(name = "endTime", description = "结束时间", required = true)
    @Parameter(name = "deviceId", description = "设备国标ID", required = false)
    public DeferredResult<WVPResult<DownloadInfo>> export(String startTime, String endTime, String deviceId) {
        DeferredResult<WVPResult<DownloadInfo>> resultDeferredResult =
                new DeferredResult<>(30 * 1000L);

        resultDeferredResult.onTimeout(() -> {
            logger.info("等待超时");
        });

        resultDeferredResult.setResult(WVPResult.success(null));

        return resultDeferredResult;
    }
}
