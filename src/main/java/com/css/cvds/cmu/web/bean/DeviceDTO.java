package com.css.cvds.cmu.web.bean;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;

/**
 * @author chend
 */
@Schema(description = "添加摄像头参数")
public class DeviceDTO {

    @Schema(description = "IP")
    @NotEmpty(message = "ip不能为空")
    private String ip;

    @Schema(description = "设备国标编号")
    private String deviceId;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "端口")
    private Integer port;

    @Schema(description = "子网掩码")
    private String netmask;

    @Schema(description = "网关")
    private String gateway;

    @Schema(description = "监视物类型")
    private Integer superviseTargetType;

    @Schema(description = "监视物ID")
    private Integer superviseTargetId;

    @Schema(description = "摄像头位置")
    private String position;

    @Schema(description = "车厢号")
    private Integer carriageNo;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getSuperviseTargetId() {
        return superviseTargetId;
    }

    public void setSuperviseTargetId(Integer superviseTargetId) {
        this.superviseTargetId = superviseTargetId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getCarriageNo() {
        return carriageNo;
    }

    public void setCarriageNo(Integer carriageNo) {
        this.carriageNo = carriageNo;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public Integer getSuperviseTargetType() {
        return superviseTargetType;
    }

    public void setSuperviseTargetType(Integer superviseTargetType) {
        this.superviseTargetType = superviseTargetType;
    }
}
