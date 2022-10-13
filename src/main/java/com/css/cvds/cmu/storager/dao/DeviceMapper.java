package com.css.cvds.cmu.storager.dao;

import com.css.cvds.cmu.gb28181.bean.Device;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用于存储设备信息
 */
@Mapper
@Repository
public interface DeviceMapper {

    @Select("SELECT * FROM device WHERE deviceId = #{deviceId}")
    Device getDeviceByDeviceId(String deviceId);

    @Insert("INSERT INTO device (" +
                "deviceId, " +
                "name, " +
                "manufacturer, " +
                "model, " +
                "firmware, " +
                "transport," +
                "streamMode," +
                "ip," +
                "port," +
                "hostAddress," +
                "netmask," +
                "gateway," +
                "expires," +
                "registerTime," +
                "keepaliveTime," +
                "createTime," +
                "updateTime," +
                "charset," +
                "subscribeCycleForCatalog," +
                "subscribeCycleForMobilePosition," +
                "mobilePositionSubmissionInterval," +
                "subscribeCycleForAlarm," +
                "ssrcCheck," +
                "geoCoordSys," +
                "treeType," +
                "superviseTargetId," +
                "online" +
            ") VALUES (" +
                "#{deviceId}," +
                "#{name}," +
                "#{manufacturer}," +
                "#{model}," +
                "#{firmware}," +
                "#{transport}," +
                "#{streamMode}," +
                "#{ip}," +
                "#{port}," +
                "#{hostAddress}," +
                "#{netmask}," +
                "#{gateway}," +
                "#{expires}," +
                "#{registerTime}," +
                "#{keepaliveTime}," +
                "#{createTime}," +
                "#{updateTime}," +
                "#{charset}," +
                "#{subscribeCycleForCatalog}," +
                "#{subscribeCycleForMobilePosition}," +
                "#{mobilePositionSubmissionInterval}," +
                "#{subscribeCycleForAlarm}," +
                "#{ssrcCheck}," +
                "#{geoCoordSys}," +
                "#{treeType}," +
                "#{superviseTargetId}" +
                "#{online}" +
            ")")
    int add(Device device);

    @Update(value = {" <script>" +
                "UPDATE device " +
                "SET updateTime='${updateTime}'" +
                "<if test=\"name != null\">, name='${name}'</if>" +
                "<if test=\"manufacturer != null\">, manufacturer='${manufacturer}'</if>" +
                "<if test=\"model != null\">, model='${model}'</if>" +
                "<if test=\"firmware != null\">, firmware='${firmware}'</if>" +
                "<if test=\"transport != null\">, transport='${transport}'</if>" +
                "<if test=\"streamMode != null\">, streamMode='${streamMode}'</if>" +
                "<if test=\"ip != null\">, ip='${ip}'</if>" +
                "<if test=\"port != null\">, port=${port}</if>" +
                "<if test=\"hostAddress != null\">, hostAddress='${hostAddress}'</if>" +
                "<if test=\"netmask != null\">, netmask='${netmask}'</if>" +
                "<if test=\"gateway != null\">, gateway='${gateway}'</if>" +
                "<if test=\"online != null\">, online=${online}</if>" +
                "<if test=\"registerTime != null\">, registerTime='${registerTime}'</if>" +
                "<if test=\"keepaliveTime != null\">, keepaliveTime='${keepaliveTime}'</if>" +
                "<if test=\"expires != null\">, expires=${expires}</if>" +
                "<if test=\"charset != null\">, charset='${charset}'</if>" +
                "<if test=\"subscribeCycleForCatalog != null\">, subscribeCycleForCatalog=${subscribeCycleForCatalog}</if>" +
                "<if test=\"subscribeCycleForMobilePosition != null\">, subscribeCycleForMobilePosition=${subscribeCycleForMobilePosition}</if>" +
                "<if test=\"mobilePositionSubmissionInterval != null\">, mobilePositionSubmissionInterval=${mobilePositionSubmissionInterval}</if>" +
                "<if test=\"subscribeCycleForAlarm != null\">, subscribeCycleForAlarm=${subscribeCycleForAlarm}</if>" +
                "<if test=\"ssrcCheck != null\">, ssrcCheck=${ssrcCheck}</if>" +
                "<if test=\"geoCoordSys != null\">, geoCoordSys=#{geoCoordSys}</if>" +
                "<if test=\"treeType != null\">, treeType=#{treeType}</if>" +
                "<if test=\"superviseTargetId != null\">, superviseTargetId='${superviseTargetId}'</if>" +
                "WHERE deviceId='${deviceId}'"+
            " </script>"})
    int update(Device device);

    @Select(value = {" <script>" +
            "SELECT " +
            "d.* " +
            "from " +
            "device d " +
            "WHERE " +
            " <if test='keyword != null'> AND (d.deviceId LIKE '%${keyword}%' OR d.name LIKE '%${keyword}%' OR d.ip LIKE '%${keyword}%')</if> " +
            " <if test='superviseTargetId != null'> AND d.superviseTargetId=#{superviseTargetId} </if> " +
            " <if test='online == true' > AND d.status=1</if>" +
            " <if test='online == false' > AND d.status=0</if>" +
            "1=1 " +
            "ORDER BY d.deviceId " +
            " </script>"})
    List<Device> getDevices(String keyword, Boolean online, Integer superviseTargetId);

    @Delete("DELETE FROM device WHERE deviceId=#{deviceId}")
    int del(String deviceId);

    @Update("UPDATE device SET online=0")
    int outlineForAll();

    @Select("SELECT * FROM device WHERE online = 1")
    List<Device> getOnlineDevices();

    @Select("SELECT * FROM device WHERE ip = #{host} AND port=${port}")
    Device getDeviceByHostAndPort(String host, int port);
}
