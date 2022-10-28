package com.css.cvds.cmu.web.bean;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author chend
 */
@Schema(description = "摄像头视频配置参数")
public class VideoConfig {

    @Schema(description = "视频编码格式化，H.264，H.265")
    private String encoder;

    @Schema(description = "视频宽度")
    private Integer width;

    @Schema(description = "视频高度")
    private Integer height;

    @Schema(description = "亮度")
    private Integer brightness;

    @Schema(description = "对比度")
    private Integer contrast;

    @Schema(description = "饱和度")
    private Integer saturation;
}
