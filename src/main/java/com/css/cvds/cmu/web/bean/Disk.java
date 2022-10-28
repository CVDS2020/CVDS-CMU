package com.css.cvds.cmu.web.bean;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author chend
 */
@Schema(description = "磁盘信息")
public class Disk {

    @Schema(description = "磁盘号")
    private String diskNo;

    @Schema(description = "类型: 0 本地，1 外挂")
    private Integer type;

    @Schema(description = "状态：0 正常, 1 故障")
    private Integer status;

    @Schema(description = "总容量")
    private long capacity;

    @Schema(description = "已使用容量")
    private long usedCapacity;

    public String getDiskNo() {
        return diskNo;
    }

    public void setDiskNo(String diskNo) {
        this.diskNo = diskNo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public long getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(long usedCapacity) {
        this.usedCapacity = usedCapacity;
    }
}
