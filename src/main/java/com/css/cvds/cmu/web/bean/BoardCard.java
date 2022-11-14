package com.css.cvds.cmu.web.bean;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author chend
 */
@Schema(description = "板卡信息")
public class BoardCard {
    @Schema(description = "板卡类型：1-电源板，2-交换板，3-视频核心板，4-AI分析板")
    private Integer type;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "状态：0-关机，1-开机")
    private Integer status;

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return  this.type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }
}
