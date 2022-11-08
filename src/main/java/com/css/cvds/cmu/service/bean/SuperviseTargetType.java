package com.css.cvds.cmu.service.bean;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author chend
 */
@Schema(description = "监视物类型信息")
public class SuperviseTargetType {

    @Schema(description = "监视物类型")
    private Integer type;

    @Schema(description = "监视物类型名称")
    private String name;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
