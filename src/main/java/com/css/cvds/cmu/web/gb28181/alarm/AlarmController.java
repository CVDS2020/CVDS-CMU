package com.css.cvds.cmu.web.gb28181.alarm;

import com.css.cvds.cmu.conf.security.SecurityUtils;
import com.css.cvds.cmu.conf.security.dto.LoginUser;
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
            @RequestParam(required = false) Long id,
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

    @Operation(summary = "分页查询报警")
    @Parameter(name = "page",description = "当前页",required = true)
    @Parameter(name = "count",description = "每页查询数量",required = true)
    @Parameter(name = "startTime",description = "开始时间")
    @Parameter(name = "endTime",description = "结束时间")
    @Parameter(name = "deviceId",description = "设备id")
    @Parameter(name = "alarmPriority",description = "告警级别")
    @Parameter(name = "alarmType",description = "告警类型")
    @Parameter(name = "already",description = "是否已处理，空/或者不填表示忽略")
    @Parameter(name = "sortField",description = "排序字段：time/priority/already")
    @Parameter(name = "sort",description = "排序方式：0 降序，1 升序")
    @GetMapping("/all")
    public WVPResult<PageInfo<DeviceAlarm>> getAll(
            @RequestParam int page,
            @RequestParam int count,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String alarmPriority,
            @RequestParam(required = false) String alarmType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Boolean already
    ) {
        if (ObjectUtils.isEmpty(alarmPriority)) {
            alarmPriority = null;
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

        return WVPResult.success(deviceAlarmService.getAllAlarm(page, count, deviceId, alarmPriority, null,
                alarmType, startTime, endTime, already));
    }

    /**
     *  查询告警类型
     *
     * @return
     */
    @Operation(summary = "查询告警类型")
    @GetMapping("/getAlarmType")
    public WVPResult<List<String>> getAlarmType() {
        return WVPResult.success(deviceAlarmService.getAlarmTypeList());
    }

    /**
     *  查询最近24小时报警
     *
     * @return
     */
    @Operation(summary = "查询最近24小时报警")
    @GetMapping("/getLast")
    public WVPResult<List<DeviceAlarm>> getLast() {
        String startTime = DateUtil.getNowMinusHours(24);
        String endTime = DateUtil.getNow();
        return WVPResult.success(deviceAlarmService.getAlarm(startTime, endTime, false));
    }

    /**
     *  设置告警状态
     *
     * @return
     */
    @Operation(summary = "设置告警状态")
    @PutMapping("/{id}/setAlready")
    @Parameter(name = "already",description = "false 未处理，true 已处理")
    public WVPResult<?> setAlready(@PathVariable Long id, @RequestParam Boolean already) {
        if (already == null || !already) {
            deviceAlarmService.updateAlready(id, null, null);
        } else {
            LoginUser userInfo = SecurityUtils.getUserInfo();
            deviceAlarmService.updateAlready(id, userInfo != null ? userInfo.getId() : 0, DateUtil.getNow());
        }
        return WVPResult.success();
    }
}
