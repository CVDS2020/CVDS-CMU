package com.css.cvds.cmu.web.bean;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 前端显示的Device信息
 * @author chend
 */
@Schema(description = "前端显示的Device信息")
public class DeviceVO {

    @Schema(description = "数据库自增ID")
    private Long id;

    @Schema(description = "设备国标编号")
    private String deviceId;

    @Schema(description = "设备名")
    private String name;

    @Schema(description = "监视物类型")
    private Integer superviseTargetType;

    @Schema(description = "监视物类型名称")
    private Integer superviseTargetTypeName;

    @Schema(description = "监视物ID")
    private Integer superviseTargetId;

    @Schema(description = "监视物名称")
    private Integer superviseTargetName;

    @Schema(description = "车厢号")
    private Integer carriageNo;

    @Schema(description = "摄像机位置")
    private Integer position;

    @Schema(description = "摄像机IP")
    private Integer ip;

    @Schema(description = "摄像机在线状态, 0 离线, 1 在线")
    private String online;

    @Schema(description = "监视物状态, 0-正常，转向架异常（1-温度异常，2-检测到异物，3-部件缺失），" +
            "受电弓姿态异常（100-降弓，101-升弓），" +
            "受电弓实体异常（201-受电弓燃弧、202-受电弓异物、203-受电弓变形、204-右弓角缺失、205-左弓角缺失），" +
            "受电弓温度异常（300-受电弓温度异常，statusText字段补充温度范围）")
    private Integer superviseTargetStatus;

    @Schema(description = "监视物状态描述")
    private String superviseTargetStatusText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSuperviseTargetType() {
        return superviseTargetType;
    }

    public void setSuperviseTargetType(Integer superviseTargetType) {
        this.superviseTargetType = superviseTargetType;
    }

    public Integer getSuperviseTargetTypeName() {
        return superviseTargetTypeName;
    }

    public void setSuperviseTargetTypeName(Integer superviseTargetTypeName) {
        this.superviseTargetTypeName = superviseTargetTypeName;
    }

    public Integer getSuperviseTargetId() {
        return superviseTargetId;
    }

    public void setSuperviseTargetId(Integer superviseTargetId) {
        this.superviseTargetId = superviseTargetId;
    }

    public Integer getSuperviseTargetName() {
        return superviseTargetName;
    }

    public void setSuperviseTargetName(Integer superviseTargetName) {
        this.superviseTargetName = superviseTargetName;
    }

    public Integer getCarriageNo() {
        return carriageNo;
    }

    public void setCarriageNo(Integer carriageNo) {
        this.carriageNo = carriageNo;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getIp() {
        return ip;
    }

    public void setIp(Integer ip) {
        this.ip = ip;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public Integer getSuperviseTargetStatus() {
        return superviseTargetStatus;
    }

    public void setSuperviseTargetStatus(Integer superviseTargetStatus) {
        this.superviseTargetStatus = superviseTargetStatus;
    }

    public String getSuperviseTargetStatusText() {
        return superviseTargetStatusText;
    }

    public void setSuperviseTargetStatusText(String superviseTargetStatusText) {
        this.superviseTargetStatusText = superviseTargetStatusText;
    }
}
