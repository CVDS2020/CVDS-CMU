package com.css.cvds.cmu.gb28181.bean;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author chend
 */
@Schema(description = "统计信息")
public class Statistics {
    @Schema(description = "板块总数")
    private int cardNum;

    @Schema(description = "板块在线数")
    private int cardOnlineNum;

    @Schema(description = "摄像机总数")
    private int cameraNum;

    @Schema(description = "摄像机在线数")
    private int cameraOnlineNum;

    @Schema(description = "存储总容量")
    private long storageCapacity;

    @Schema(description = "存储已使用容量")
    private long storageUsedCapacity;

    public int getCardNum() {
        return cardNum;
    }

    public void setCardNum(int cardNum) {
        this.cardNum = cardNum;
    }

    public int getCardOnlineNum() {
        return cardOnlineNum;
    }

    public void setCardOnlineNum(int cardOnlineNum) {
        this.cardOnlineNum = cardOnlineNum;
    }

    public int getCameraNum() {
        return cameraNum;
    }

    public void setCameraNum(int cameraNum) {
        this.cameraNum = cameraNum;
    }

    public int getCameraOnlineNum() {
        return cameraOnlineNum;
    }

    public void setCameraOnlineNum(int cameraOnlineNum) {
        this.cameraOnlineNum = cameraOnlineNum;
    }

    public long getStorageCapacity() {
        return storageCapacity;
    }

    public void setStorageCapacity(long storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    public long getStorageUsedCapacity() {
        return storageUsedCapacity;
    }

    public void setStorageUsedCapacity(long storageUsedCapacity) {
        this.storageUsedCapacity = storageUsedCapacity;
    }
}
