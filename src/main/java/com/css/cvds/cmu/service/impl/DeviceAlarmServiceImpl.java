package com.css.cvds.cmu.service.impl;

import com.css.cvds.cmu.service.IUserService;
import com.css.cvds.cmu.storager.dao.DeviceAlarmMapper;
import com.css.cvds.cmu.gb28181.bean.DeviceAlarm;
import com.css.cvds.cmu.service.IDeviceAlarmService;
import com.css.cvds.cmu.storager.dao.dto.User;
import com.css.cvds.cmu.utils.CollectUtils;
import com.css.cvds.cmu.web.bean.DeviceAlarmVO;
import com.css.cvds.cmu.web.converter.DeviceAlarmConverter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DeviceAlarmServiceImpl implements IDeviceAlarmService {

    @Autowired
    private DeviceAlarmMapper deviceAlarmMapper;

    @Autowired
    private IUserService userService;

    private DeviceAlarmVO toVO(DeviceAlarm entity) {
        DeviceAlarmVO vo = DeviceAlarmConverter.INSTANCE.toVo(entity);
        if (Objects.nonNull(vo.getAlreadyUser())) {
            User user = userService.getUser(vo.getAlreadyUser());
            if (Objects.isNull(user)) {
                vo.setAlreadyUserName(user.getUsername());
            }
        }
        return vo;
    }

    @Override
    public PageInfo<DeviceAlarmVO> getAllAlarm(int page, int count, String deviceId, String alarmPriority, String alarmMethod,
                                               String alarmType, String startTime, String endTime, Boolean already, String sortField, String sortMethod) {
        PageHelper.startPage(page, count);
        List<DeviceAlarm> all = deviceAlarmMapper.query(deviceId, alarmPriority, alarmMethod, alarmType, startTime, endTime, already, sortField, sortMethod);
        return new PageInfo<>(CollectUtils.toList(all, this::toVO));
    }

    @Override
    public List<DeviceAlarmVO> getAlarm(String startTime, String endTime, Boolean already) {
        List<DeviceAlarm> all = deviceAlarmMapper.query(null, null, null, null, startTime, endTime, already, "alarmTime", "DESC");
        return CollectUtils.toList(all, this::toVO);
    }

    @Override
    public List<String> getAlarmTypeList() {
        return deviceAlarmMapper.queryAlarmType();
    }

    @Override
    public void add(DeviceAlarm deviceAlarm) {
        deviceAlarmMapper.add(deviceAlarm);
    }

    @Override
    public int clearAlarmBeforeTime(Long id, List<String> deviceIdList, String time) {
        return deviceAlarmMapper.clearAlarmBeforeTime(id, deviceIdList, time);
    }

    @Override
    public int updateAlready(Long id, Integer alreadyUser, String alreadyTime) {
        return deviceAlarmMapper.updateAlready(id, alreadyUser, alreadyTime);
    }
}
