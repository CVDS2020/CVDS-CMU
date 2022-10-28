package com.css.cvds.cmu.web.bean;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author chend
 */
@Schema(description = "存储配置参数")
public class StorageConfig {

    @Schema(description = "文件保持间隔，单位：分钟")
    private Integer saveInterval;

    @Schema(description = "保存目录")
    private String saveDirectory;

    @Schema(description = "受电弓视频存储时长，单位: 天")
    private Integer saveDurationPantograph;

    @Schema(description = "车厢视频存储时长，单位: 天")
    private Integer saveDurationCarriage;

    @Schema(description = "车厢视频存储时长，单位: 天")
    private Integer saveDurationCard;
}
