package com.zistone.gprs.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationInfo {
    private static final SimpleDateFormat SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String toString() {
        return "LocationInfo{" +
                "id=" + id +
                ", deviceId='" + deviceId + '\'' +
                ", lat=" + lat +
                ", lot=" + lot +
                ", height=" + height +
                ", createTime=" + (createTime != null ? SIMPLEDATEFORMAT.format(createTime) : null) +
                '}';
    }

    /**
     * 自增主键(由数据库生成)
     */
    private int id;

    /**
     * 设备编号,设备自带
     */
    private String deviceId;

    /**
     * 纬度
     */
    private double lat;

    /**
     * 经度
     */
    private double lot;

    /**
     * 海拔
     */
    private int height;

    /**
     * 创建时间(由前端上传)
     */
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLot() {
        return lot;
    }

    public void setLot(double lot) {
        this.lot = lot;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
