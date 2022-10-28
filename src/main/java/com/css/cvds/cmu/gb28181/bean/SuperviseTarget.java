package com.css.cvds.cmu.gb28181.bean;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author chend
 */
@Schema(description = "监视物信息")
public class SuperviseTarget {

    @Schema(description = "id")
    private Integer id;

    @Schema(description = "监视物名称")
    private String name;

    @Schema(description = "监视物类型")
    private Integer type;

    @Schema(description = "监视物所在车厢")
    private Integer carriageNo;

    @Schema(description = "安装位置")
    private String address;

    @Schema(description = "状态，0-正常，" +
            "转向架异常（1-温度异常，2-检测到异物，3-部件缺失），" +
            "受电弓姿态异常（100-降弓，101-升弓），" +
            "受电弓实体异常（201-受电弓燃弧、202-受电弓异物、203-受电弓变形、204-右弓角缺失、205-左弓角缺失），" +
            "受电弓温度异常（300-受电弓温度异常，statusText字段补充温度范围）")
    private Integer status;

    @Schema(description = "安装位置")
    private String statusText;

    @Schema(description = "描述")
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCarriageNo() {
        return carriageNo;
    }

    public void setCarriageNo(Integer carriageNo) {
        this.carriageNo = carriageNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
