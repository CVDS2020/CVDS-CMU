package com.css.cvds.cmu.web.gb28181.device.bean;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author chend
 */
@Schema(description = "添加摄像头参数")
public class AddDeviceParam {

    /**
     * 设备国标编号
     */
    @Schema(description = "设备国标编号")
    private String deviceId;

    /**
     * 设备名
     */
    @Schema(description = "名称")
    private String name;

    /**
     * wan地址_ip
     */
    @Schema(description = "IP")
    private String  ip;

    /**
     * wan地址_port
     */
    @Schema(description = "端口")
    private Integer port;

    /**
     * 子网掩码
     */
    @Schema(description = "子网掩码")
    private Integer netmask;

    /**
     * 网关
     */
    @Schema(description = "网关")
    private Integer gateway;

    /**
     * 监视物ID
     */
    @Schema(description = "监视物ID")
    private Integer superviseTargetId;

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

    public Integer getNetmask() {
        return netmask;
    }

    public void setNetmask(Integer netmask) {
        this.netmask = netmask;
    }

    public Integer getGateway() {
        return gateway;
    }

    public void setGateway(Integer gateway) {
        this.gateway = gateway;
    }
}
