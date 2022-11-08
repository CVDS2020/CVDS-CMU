package com.css.cvds.cmu.storager.dao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author chend
 */
@Schema(description = "车辆信息")
public class TrainDto {

    @Schema(description = "车次")
    private String trainNo;

    @Schema(description = "车型")
    private String model;

    @Schema(description = "车厢数")
    private Integer carriageNum;

    @Schema(description = "名称")
    private String name;

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
}
