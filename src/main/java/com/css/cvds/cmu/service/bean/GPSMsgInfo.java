package com.css.cvds.cmu.service.bean;

import io.swagger.v3.oas.annotations.media.Schema;

public class GPSMsgInfo {

    /**
     *
     */
    private String id;

    /**
     * 经度 (必选)
     */
    private double lng;

    /**
     * 纬度 (必选)
     */
    private double lat;

    /**
     * 速度,单位:km/h (可选)
     */
    private double speed;

    /**
     * 产生通知时间, 时间格式： 2020-01-14T14:32:12
     */
    private String time;

    /**
     * 方向,取值为当前摄像头方向与正北方的顺时针夹角,取值范围0°~360°,单位:(°)(可选)
     */
    private String direction;

    /**
     * 海拔高度,单位:m(可选)
     */
    private String altitude;

    private boolean stored;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public boolean isStored() {
        return stored;
    }

    public void setStored(boolean stored) {
        this.stored = stored;
    }

    /**
     * @author chend
     */
    @Schema(description = "机车信息")
    public static class Train {
        @Schema(description = "车次")
        private String trainNo;

        @Schema(description = "车型")
        private String model;

        @Schema(description = "车厢数")
        private Integer carriageNum;

        @Schema(description = "名称")
        private String name;

        @Schema(description = "经度")
        private double longitude;

        @Schema(description = "纬度")
        private double latitude;

        @Schema(description = "描述")
        private String description;

        public String getTrainNo() {
            return trainNo;
        }

        public void setTrainNo(String trainNo) {
            this.trainNo = trainNo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public Integer getCarriageNum() {
            return carriageNum;
        }

        public void setCarriageNum(Integer carriageNum) {
            this.carriageNum = carriageNum;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
    }
}
