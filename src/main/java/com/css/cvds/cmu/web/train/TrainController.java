package com.css.cvds.cmu.web.train;

import com.css.cvds.cmu.conf.exception.ControllerException;
import com.css.cvds.cmu.conf.security.SecurityUtils;
import com.css.cvds.cmu.conf.security.dto.LoginUser;
import com.css.cvds.cmu.service.ITrainService;
import com.css.cvds.cmu.storager.dao.dto.TrainDto;
import com.css.cvds.cmu.web.bean.ErrorCode;
import com.css.cvds.cmu.web.bean.WVPResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Tag(name  = "列车管理")
@CrossOrigin
@RestController
@RequestMapping("/api/tran")
public class TrainController {

    @Autowired
    private ITrainService trainService;

    @PostMapping("/update")
    @Operation(summary = "更新车辆信息")
    @Parameter(name = "name", description = "名称")
    @Parameter(name = "serial", description = "机车号")
    @Parameter(name = "trainNo", description = "车次")
    @Parameter(name = "description", description = "备注")
    public WVPResult<?> update(@RequestParam String serial,
                               @RequestParam String trainNo,
                               @RequestParam String name,
                               @RequestParam String description) {
        if (!SecurityUtils.isAdmin()) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
        }
        LoginUser userInfo = SecurityUtils.getUserInfo();
        if (userInfo== null) {
            throw new ControllerException(ErrorCode.ERROR100);
        }
        try {
            int ret = 0;
            TrainDto dto = new TrainDto();
            if (Objects.nonNull(serial)) {
                dto.setSerial(serial);
                ret ++;
            }
            if (Objects.nonNull(trainNo)) {
                dto.setTrainNo(trainNo);
                ret ++;
            }
            if (Objects.nonNull(name)) {
                dto.setName(name);
                ret ++;
            }
            if (Objects.nonNull(description)) {
                dto.setDescription(description);
                ret ++;
            }
            if (ret > 0) {
                trainService.update(dto);
            }
        } catch (Exception e) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), e.getMessage());
        }
        return WVPResult.success(null);
    }

    @GetMapping("/get")
    @Operation(summary = "获取列车信息")
    public WVPResult<TrainDto> get(){
        return WVPResult.success(trainService.get());
    }
}
