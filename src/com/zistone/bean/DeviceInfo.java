package com.zistone.bean;

import java.util.Date;

public class DeviceInfo
{
    public DeviceInfo()
    {
    }

    /**
     * 设备编号(由数据库生成)
     */
    private int m_id;

    public int getM_id()
    {
        return m_id;
    }

    public void setM_id(int m_id)
    {
        this.m_id = m_id;
    }

    /**
     * 设备名
     */
    private String m_deviceName;

    public String getM_deviceName()
    {
        return m_deviceName;
    }

    public void setM_deviceName(String m_deviceName)
    {
        this.m_deviceName = m_deviceName;
    }

    /**
     * 设备类型
     */
    public String m_type;

    public String getM_type()
    {
        return m_type;
    }

    public void setM_type(String m_type)
    {
        this.m_type = m_type;
    }

    /**
     * 设备状态
     */
    private int m_state;

    public int getM_state()
    {
        return m_state;
    }

    public void setM_state(int m_state)
    {
        this.m_state = m_state;
    }

    /**
     * 纬度
     */
    private double m_lat;

    public double getM_lat()
    {
        return m_lat;
    }

    public void setM_lat(double m_lat)
    {
        this.m_lat = m_lat;
    }

    /**
     * 经度
     */
    private double m_lot;

    public double getM_lot()
    {
        return m_lot;
    }

    public void setM_lot(double m_lot)
    {
        this.m_lot = m_lot;
    }

    /**
     * 创建时间
     */
    private Date m_craeteTime;

    public Date getM_craeteTime()
    {
        return m_craeteTime;
    }

    public void setM_craeteTime(Date m_craeteTime)
    {
        this.m_craeteTime = m_craeteTime;
    }

    /**
     * 修改时间
     */
    private Date m_updateTime;

    public Date getM_updateTime()
    {
        return m_updateTime;
    }

    public void setM_updateTime(Date m_updateTime)
    {
        this.m_updateTime = m_updateTime;
    }

    /**
     * 描述
     */
    private String m_description;

    public String getM_description()
    {
        return m_description;
    }

    public void setM_description(String m_description)
    {
        this.m_description = m_description;
    }
}
