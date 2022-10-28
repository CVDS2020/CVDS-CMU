package com.css.cvds.cmu.service.impl;

import com.css.cvds.cmu.storager.dao.DeviceAlarmMapper;
import com.css.cvds.cmu.gb28181.bean.DeviceAlarm;
import com.css.cvds.cmu.service.IDeviceAlarmService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceAlarmServiceImpl implements IDeviceAlarmService {

    @Autowired
    private DeviceAlarmMapper deviceAlarmMapper;

    @Override
    public PageInfo<DeviceAlarm> getAllAlarm(int page, int count, String deviceId, String alarmPriority,
                                             String alarmMethod, String alarmType, String startTime, String endTime, Boolean already) {
        PageHelper.startPage(page, count);
        List<DeviceAlarm> all = deviceAlarmMapper.query(deviceId, alarmPriority, alarmMethod, alarmType, startTime, endTime, already);
        return new PageInfo<>(all);
    }

    @Override
    public List<DeviceAlarm> getAlarm(String startTime, String endTime, Boolean already) {
        return deviceAlarmMapper.query(null, null, null, null, startTime, endTime, already);
    }

    @Override
    public List<String> getAlarmTypeList() {
        return Lists.newArrayList();
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
