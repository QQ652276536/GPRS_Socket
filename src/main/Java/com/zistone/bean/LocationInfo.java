package com.zistone.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationInfo
{
    private static final SimpleDateFormat SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String toString()
    {
        return "LocationInfo{" + "m_id=" + m_id + ", m_deviceId='" + m_deviceId + '\'' + ", m_lat=" + m_lat + ", m_lot=" + m_lot + ", " + "m_height=" + m_height + ", m_createTime='" + SIMPLEDATEFORMAT
                .format(m_createTime) + '\'' + '}';
    }

    /**
     * 自增主键(由数据库生成)
     */
    private int m_id;

    /**
     * 设备编号,设备自带
     */
    private String m_deviceId;

    /**
     * 纬度
     */
    private double m_lat;

    /**
     * 经度
     */
    private double m_lot;

    /**
     * 海拔
     */
    private int m_height;

    /**
     * 创建时间
     */
    private Date m_createTime;

    public int getM_id()
    {
        return m_id;
    }

    public void setM_id(int m_id)
    {
        this.m_id = m_id;
    }

    public String getM_deviceId()
    {
        return m_deviceId;
    }

    public void setM_deviceId(String m_deviceId)
    {
        this.m_deviceId = m_deviceId;
    }

    public double getM_lat()
    {
        return m_lat;
    }

    public void setM_lat(double m_lat)
    {
        this.m_lat = m_lat;
    }

    public double getM_lot()
    {
        return m_lot;
    }

    public void setM_lot(double m_lot)
    {
        this.m_lot = m_lot;
    }

    public Date getM_createTime()
    {
        return m_createTime;
    }

    public void setM_createTime(Date m_createTime)
    {
        this.m_createTime = m_createTime;
    }

    public int getM_height()
    {
        return m_height;
    }

    public void setM_height(int m_height)
    {
        this.m_height = m_height;
    }
}
