package com.zistone.bean;

public class LocationInfo
{
    @Override
    public String toString()
    {
        return "LocationInfo{" + "m_id='" + m_id + '\'' + ", m_deviceId='" + m_deviceId + '\'' + ", m_lat=" + m_lat + ", m_lot=" + m_lot + ", m_createTime=" + m_createTime + '}';
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
     * 创建时间(由前端上传)
     */
    private String m_createTime;

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

    public String getM_createTime()
    {
        return m_createTime;
    }

    public void setM_createTime(String m_createTime)
    {
        this.m_createTime = m_createTime;
    }
}
