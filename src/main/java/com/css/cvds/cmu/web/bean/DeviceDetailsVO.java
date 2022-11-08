package com.css.cvds.cmu.web.bean;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 前端显示的Device详情信息
 * @author chend
 */
@Schema(description = "前端显示的Device信息")
public class DeviceDetailsVO extends DeviceVO {

    @Schema(description = "视频配置")
    private VideoConfig videoConfig;

    public VideoConfig getVideoConfig() {
        return videoConfig;
    }

    public void setVideoConfig(VideoConfig videoConfig) {
        this.videoConfig = videoConfig;
    }
}
