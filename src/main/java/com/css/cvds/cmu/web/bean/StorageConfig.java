package com.css.cvds.cmu.web.bean;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author chend
 */
@Schema(description = "存储配置参数")
public class StorageConfig {

    @Schema(description = "文件保持间隔，单位：分钟")
    private Integer saveInterval;

    @Schema(description = "保存位置")
    private String saveDirectory;

    @Schema(description = "受电弓视频存储时长，单位: 天")
    private Integer saveDurationPantograph;

    @Schema(description = "车厢视频存储时长，单位: 天")
    private Integer saveDurationCarriage;

    @Schema(description = "车厢视频存储时长，单位: 天")
    private Integer saveDurationCard;

    public Integer getSaveInterval() {
        return saveInterval;
    }

    public void setSaveInterval(Integer saveInterval) {
        this.saveInterval = saveInterval;
    }

    public String getSaveDirectory() {
        return saveDirectory;
    }

    public void setSaveDirectory(String saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    public Integer getSaveDurationPantograph() {
        return saveDurationPantograph;
    }

    public void setSaveDurationPantograph(Integer saveDurationPantograph) {
        this.saveDurationPantograph = saveDurationPantograph;
    }

    public Integer getSaveDurationCarriage() {
        return saveDurationCarriage;
    }

    public void setSaveDurationCarriage(Integer saveDurationCarriage) {
        this.saveDurationCarriage = saveDurationCarriage;
    }

    public Integer getSaveDurationCard() {
        return saveDurationCard;
    }

    public void setSaveDurationCard(Integer saveDurationCard) {
        this.saveDurationCard = saveDurationCard;
    }
}
