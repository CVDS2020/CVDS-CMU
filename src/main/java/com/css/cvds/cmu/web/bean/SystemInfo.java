package com.css.cvds.cmu.web.bean;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author chend
 */
@Schema(description = "系统信息参数")
public class SystemInfo {

    @Schema(description = "类型代码")
    private String typeCode;

    @Schema(description = "厂商代码")
    private String manufacturerCode;

    @Schema(description = "软件版本")
    private String softwareVersion;

    @Schema(description = "固件版本")
    private String firmwareVersion;

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getManufacturerCode() {
        return manufacturerCode;
    }

    public void setManufacturerCode(String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
}
