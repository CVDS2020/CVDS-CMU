package com.css.cvds.cmu.web.supervise;

import com.css.cvds.cmu.gb28181.bean.SuperviseTarget;
import com.css.cvds.cmu.gb28181.bean.SuperviseTargetType;
import com.css.cvds.cmu.service.ISuperviseTarget;
import com.css.cvds.cmu.web.bean.WVPResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name  = "监视物管理")
@CrossOrigin
@RestController
@RequestMapping("/api/supervise")
public class SuperviseTargetController {

    @Autowired
    private ISuperviseTarget superviseTarget;

    @GetMapping("/list")
    @Operation(summary = "获取监视物列表")
    public WVPResult<List<SuperviseTarget>> get(@RequestParam(required = false) Integer type) {
        return WVPResult.success(superviseTarget.getList(type));
    }

    @GetMapping("/type/list")
    @Operation(summary = "获取监视物列表")
    public WVPResult<List<SuperviseTargetType>> getType() {
        return WVPResult.success(superviseTarget.getTypeList());
    }
}
