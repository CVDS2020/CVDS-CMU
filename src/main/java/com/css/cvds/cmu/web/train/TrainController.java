package com.css.cvds.cmu.web.train;

import com.css.cvds.cmu.gb28181.bean.Train;
import com.css.cvds.cmu.service.ITrainService;
import com.css.cvds.cmu.web.bean.WVPResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name  = "列车管理")
@CrossOrigin
@RestController
@RequestMapping("/api/train")
public class TrainController {

    @Autowired
    private ITrainService trainService;

    @GetMapping("/get")
    @Operation(summary = "获取列车信息")
    public WVPResult<Train> get(){
        return WVPResult.success(trainService.get());
    }
}
