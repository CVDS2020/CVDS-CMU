package com.css.cvds.cmu.web.record;

import com.alibaba.fastjson.JSONObject;
import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.service.IRecordInfoServer;
import com.css.cvds.cmu.storager.dao.dto.RecordInfo;
import com.css.cvds.cmu.web.bean.WVPResult;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name  = "云端录像")
@CrossOrigin
@RestController
@RequestMapping("/api/record")
public class RecordController {

    @Autowired
    private IRecordInfoServer recordInfoServer;

    // @Operation(summary = "录像列表查询")
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

    // @Operation(summary = "录像详情")
    @GetMapping(value = "/detail/{id}")
    @Parameter(name = "id", description = "录像id", required = true)
    public WVPResult<RecordInfo> detail(@PathVariable String id){
        return null;
    }
}
