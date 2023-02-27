package com.css.cvds.cmu.storager.dao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * @author chend
 */
@Schema(description = "终端信息")
public class Terminal {
    @Schema(description = "终端名")
    private String name;

    public String getUsername() {
        return name;
    }

    public void setUsername(String username) {
        this.name = username;
    }

}
