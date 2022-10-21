package com.css.cvds.cmu.web.gb28181.alarm;

import com.css.cvds.cmu.storager.dao.dto.Role;
import com.css.cvds.cmu.utils.DateUtil;
import com.css.cvds.cmu.conf.exception.ControllerException;
import com.css.cvds.cmu.gb28181.bean.DeviceAlarm;
import com.css.cvds.cmu.service.IDeviceAlarmService;
import com.css.cvds.cmu.web.bean.ErrorCode;
import com.css.cvds.cmu.web.bean.WVPResult;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "报警信息管理")
@CrossOrigin
@RestController
@RequestMapping("/api/alarm")
public class AlarmController {

    private final static Logger logger = LoggerFactory.getLogger(AlarmController.class);

    @Autowired
    private IDeviceAlarmService deviceAlarmService;

    /**
     *  删除报警
     *
     * @param id 报警id
     * @param deviceIds 多个设备id,逗号分隔
     * @param time 结束时间(这个时间之前的报警会被删除)
     * @return
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除报警")
    @Parameter(name = "id", description = "ID")
    @Parameter(name = "deviceIds", description = "多个设备id,逗号分隔")
    @Parameter(name = "time", description = "结束时间")
    public WVPResult<List<Role>> delete(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String deviceIds,
            @RequestParam(required = false) String time
    ) {
        if (ObjectUtils.isEmpty(id)) {
            id = null;
        }
        if (ObjectUtils.isEmpty(deviceIds)) {
            deviceIds = null;
        }
        if (ObjectUtils.isEmpty(time)) {
            time = null;
        }
        if (!DateUtil.verification(time, DateUtil.formatter) ){
            return null;
        }
        List<String> deviceIdList = null;
        if (deviceIds != null) {
            String[] deviceIdArray = deviceIds.split(",");
            deviceIdList = Arrays.asList(deviceIdArray);
        }
        deviceAlarmService.clearAlarmBeforeTime(id, deviceIdList, time);

        return WVPResult.success();
    }

    /**
     *  分页查询报警
     *
     * @param deviceId 设备id
     * @param page 当前页
     * @param count 每页查询数量
     * @param alarmPriority  报警级别
     * @param alarmMethod 报警方式
     * @param alarmType  报警类型
     * @param startTime  开始时间
     * @param endTime 结束时间
     * @return
     */
    @Operation(summary = "分页查询报警")
    @Parameter(name = "page",description = "当前页",required = true)
    @Parameter(name = "count",description = "每页查询数量",required = true)
    @Parameter(name = "deviceId",description = "设备id")
    @Parameter(name = "alarmPriority",description = "查询内容")
    @Parameter(name = "alarmMethod",description = "查询内容")
    @Parameter(name = "alarmType",description = "每页查询数量")
    @Parameter(name = "startTime",description = "开始时间")
    @Parameter(name = "endTime",description = "结束时间")
    @GetMapping("/all")
    public WVPResult<PageInfo<DeviceAlarm>> getAll(
            @RequestParam int page,
            @RequestParam int count,
            @RequestParam(required = false)  String deviceId,
            @RequestParam(required = false) String alarmPriority,
            @RequestParam(required = false) String alarmMethod,
            @RequestParam(required = false) String alarmType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        if (ObjectUtils.isEmpty(alarmPriority)) {
            alarmPriority = null;
        }
        if (ObjectUtils.isEmpty(alarmMethod)) {
            alarmMethod = null;
        }
        if (ObjectUtils.isEmpty(alarmType)) {
            alarmType = null;
        }
        if (ObjectUtils.isEmpty(startTime)) {
            startTime = null;
        }
        if (ObjectUtils.isEmpty(endTime)) {
            endTime = null;
        }

        if (!DateUtil.verification(startTime, DateUtil.formatter) || !DateUtil.verification(endTime, DateUtil.formatter)){
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "开始时间或结束时间格式有误");
        }

        return WVPResult.success(deviceAlarmService.getAllAlarm(page, count, deviceId, alarmPriority, alarmMethod,
                alarmType, startTime, endTime));
    }
}
