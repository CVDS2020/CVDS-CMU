package com.css.cvds.cmu.storager.dao;

import com.css.cvds.cmu.gb28181.bean.Device;
import com.css.cvds.cmu.gb28181.bean.DeviceAlarm;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用于存储设备的报警信息
 */
@Mapper
@Repository
public interface DeviceAlarmMapper {

    @Insert("INSERT INTO device_alarm (deviceId, channelId, alarmPriority, alarmMethod, alarmTime, alarmDescription, longitude, latitude, alarmType , createTime ) " +
            "VALUES ('${deviceId}', '${channelId}', '${alarmPriority}', '${alarmMethod}', '${alarmTime}', '${alarmDescription}', ${longitude}, ${latitude}, '${alarmType}', '${createTime}')")
    int add(DeviceAlarm alarm);

    @Select(value = {" <script>" +
            " SELECT * FROM device_alarm " +
            " WHERE 1=1 " +
            " <if test=\"deviceId != null\" >  AND deviceId = '${deviceId}'</if>" +
            " <if test=\"alarmPriority != null\" >  AND alarmPriority = '${alarmPriority}' </if>" +
            " <if test=\"alarmMethod != null\" >  AND alarmMethod = '${alarmMethod}' </if>" +
            " <if test=\"alarmType != null\" >  AND alarmType = '${alarmType}' </if>" +
            " <if test=\"startTime != null\" >  AND alarmTime &gt;= '${startTime}' </if>" +
            " <if test=\"endTime != null\" >  AND alarmTime &lt;= '${endTime}' </if>" +
            " <if test=\"already != null and already\" >  AND (alreadyTime != null AND alreadyTime != '') </if>" +
            " <if test=\"already != null and !already\" >  AND (alreadyTime == null OR alreadyTime == '') </if>" +
            " ORDER BY alarmTime ASC " +
            " </script>"})
    List<DeviceAlarm> query(String deviceId, String alarmPriority, String alarmMethod,
                            String alarmType, String startTime, String endTime, Boolean already);


    @Delete(" <script>" +
            "DELETE FROM device_alarm WHERE 1=1 " +
            " <if test=\"deviceIdList != null and id == null \" > AND deviceId in " +
            "<foreach collection='deviceIdList'  item='item'  open='(' separator=',' close=')' > '${item}'</foreach>" +
            "</if>" +
            " <if test=\"time != null and id == null \" > AND alarmTime &lt;= '${time}'</if>" +
            " <if test=\"id != null\" > AND id = ${id}</if>" +
            " </script>"
            )
    int clearAlarmBeforeTime(Long id, List<String> deviceIdList, String time);

    @Update(value = {"UPDATE device_alarm SET alreadyUser=#{alreadyUser}, alreadyTime=#{alreadyTime} WHERE id=#{id}"})
    int updateAlready(Long id, Integer alreadyUser, String alreadyTime);
}
