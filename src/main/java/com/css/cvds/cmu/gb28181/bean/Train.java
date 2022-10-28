package com.css.cvds.cmu.gb28181.bean;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author chend
 */
@Schema(description = "机车信息")
public class Train {
    @Schema(description = "车次")
    private String trainNo;

    @Schema(description = "车型")
    private String model;

    @Schema(description = "车厢数")
    private Integer carriageNum;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "经度")
    private double longitude;

    @Schema(description = "纬度")
    private double latitude;

    @Schema(description = "描述")
    private String description;

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getCarriageNum() {
        return carriageNum;
    }

    public void setCarriageNum(Integer carriageNum) {
        this.carriageNum = carriageNum;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
