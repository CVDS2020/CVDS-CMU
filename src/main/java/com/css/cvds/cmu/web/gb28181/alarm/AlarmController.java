package com.css.cvds.cmu.web.gb28181.alarm;

import com.css.cvds.cmu.conf.security.SecurityUtils;
import com.css.cvds.cmu.conf.security.dto.LoginUser;
import com.css.cvds.cmu.service.ILogService;
import com.css.cvds.cmu.storager.dao.dto.Role;
import com.css.cvds.cmu.utils.DateUtil;
import com.css.cvds.cmu.conf.exception.ControllerException;
import com.css.cvds.cmu.gb28181.bean.DeviceAlarm;
import com.css.cvds.cmu.service.IDeviceAlarmService;
import com.css.cvds.cmu.utils.UserLogEnum;
import com.css.cvds.cmu.web.bean.DeviceAlarmVO;
import com.css.cvds.cmu.web.bean.ErrorCode;
import com.css.cvds.cmu.web.bean.WVPResult;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Tag(name = "报警信息管理")
@CrossOrigin
@RestController
@RequestMapping("/api/alarm")
public class AlarmController {

    private final static Logger logger = LoggerFactory.getLogger(AlarmController.class);

    @Autowired
    private IDeviceAlarmService deviceAlarmService;

    @Autowired
    private ILogService logService;

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
        if (!SecurityUtils.isAdmin()) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "没有权限进行此项操作");
        }
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

        logService.addUserLog(UserLogEnum.DATA_CONFIG, "删除告警");

        return WVPResult.success();
    }

    @Operation(summary = "分页查询报警")
    @Parameter(name = "page",description = "当前页", required = true)
    @Parameter(name = "count",description = "每页查询数量", required = true)
    @Parameter(name = "startTime",description = "开始时间", required = true)
    @Parameter(name = "endTime",description = "结束时间", required = true)
    @Parameter(name = "deviceId",description = "设备id")
    @Parameter(name = "alarmPriority",description = "告警级别")
    @Parameter(name = "alarmType",description = "告警类型")
    @Parameter(name = "already",description = "是否已处理，空/或者不填表示忽略")
    @Parameter(name = "sortField",description = "排序字段：time/priority/already")
    @Parameter(name = "sort",description = "排序方式：0 降序，1 升序")
    @GetMapping("/list")
    public WVPResult<PageInfo<DeviceAlarmVO>> getList(
            @RequestParam int page,
            @RequestParam int count,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String alarmPriority,
            @RequestParam(required = false) String alarmType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Boolean already,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) Integer sort
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
        if (Objects.equals("priority", sortField)) {
            sortField = "alarmPriority";
        } else if (Objects.equals("already", sortField)) {
            sortField = "already";
        } else {
            sortField = "alarmTime";
        }
        // 默认降序
        String sortMethod = "DESC";
        if (Objects.equals(sort, 1)) {
            sortMethod = "ASC";
        }

        if (!ObjectUtils.isEmpty(startTime)) {
            if (!DateUtil.verification(startTime, DateUtil.formatter)) {
                throw new ControllerException(ErrorCode.ERROR100.getCode(), "开始时间格式有误");
            }
        }

        if (!ObjectUtils.isEmpty(endTime)) {
            if (!DateUtil.verification(endTime, DateUtil.formatter)) {
                throw new ControllerException(ErrorCode.ERROR100.getCode(), "结束时间格式有误");
            }
        }

        return WVPResult.success(deviceAlarmService.getAllAlarm(page, count, deviceId, alarmPriority, null,
                alarmType, startTime, endTime, already, sortField, sortMethod));
    }

    /**
     *  查询告警类型
     *
     * @return
     */
    @Operation(summary = "查询告警类型")
    @GetMapping("/type")
    public WVPResult<List<String>> getAlarmType() {

        return WVPResult.success(deviceAlarmService.getAlarmTypeList());
    }

    /**
     *  查询告警级别
     *
     * @return
     */
    @Operation(summary = "查询告警级别")
    @GetMapping("/priority")
    public WVPResult<List<String>> getAlarmPriority() {
        return WVPResult.success(deviceAlarmService.getAlarmTypeList());
    }

    /**
     *  查询最近24小时报警
     *
     * @return
     */
    @Operation(summary = "查询最近24小时报警")
    @GetMapping("/last")
    public WVPResult<List<DeviceAlarmVO>> getLast() {
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
    @PutMapping("/{id}/status")
    @Parameter(name = "already",description = "false 未处理，true 已处理")
    public WVPResult<?> setAlready(@PathVariable Long id, @RequestParam Boolean already) {
        if (already == null || !already) {
            int ret = deviceAlarmService.updateAlready(id, null, null);
            if (ret > 0) {
                logService.addUserLog(UserLogEnum.ALARM_ALREADY, "更新告警状态为未确认");
            }
        } else {
            LoginUser userInfo = SecurityUtils.getUserInfo();
            int ret = deviceAlarmService.updateAlready(id, userInfo != null ? userInfo.getId() : 0, DateUtil.getNow());
            if (ret > 0) {
                logService.addUserLog(UserLogEnum.ALARM_ALREADY, "确认告警");
            }
        }
        return WVPResult.success();
    }
}
