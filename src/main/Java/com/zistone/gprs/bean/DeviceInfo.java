package com.zistone.gprs.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DeviceInfo {
    private static final SimpleDateFormat SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "id=" + id +
                ", deviceId='" + deviceId + '\'' +
                ", sim='" + sim + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", state=" + state +
                ", lat=" + lat +
                ", lot=" + lot +
                ", height=" + height +
                ", createTime=" + (createTime != null ? SIMPLEDATEFORMAT.format(createTime) : null) +
                ", updateTime=" + (updateTime != null ? SIMPLEDATEFORMAT.format(updateTime) : null) +
                ", comment='" + comment + '\'' +
                ", akCode='" + akCode + '\'' +
                ", temperature=" + temperature +
                ", electricity=" + electricity +
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
     * SIM卡号
     */
    private String sim;

    /**
     * 设备名
     */
    private String name;

    /**
     * 设备类型
     */
    public String type;

    /**
     * 设备状态
     */
    private int state;

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String comment;

    /**
     * 鉴权码
     */
    private String akCode;

    /**
     * 温度
     */
    private int temperature;

    /**
     * 剩余电量
     */
    private int electricity;

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

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAkCode() {
        return akCode;
    }

    public void setAkCode(String akCode) {
        this.akCode = akCode;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getElectricity() {
        return electricity;
    }

    public void setElectricity(int electricity) {
        this.electricity = electricity;
    }

}
