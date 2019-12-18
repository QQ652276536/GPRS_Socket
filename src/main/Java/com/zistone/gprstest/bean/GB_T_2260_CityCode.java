package com.zistone.gprstest.bean;

/**
 * 《中华人民共和国行政区划代码》的市级代码
 */
public class GB_T_2260_CityCode
{
    @Override
    public String toString()
    {
        return "GB_T_2260_CityCode{" + "m_id=" + m_id + ", m_code=" + m_code + ", m_name='" + m_name + '\'' + ", m_parentProvinceId=" + m_parentProvinceId + '}';
    }

    /**
     * 城市编号(手动生成)
     */
    private int m_id;

    /**
     * 城市编码
     */
    private int m_code;

    /**
     * 城市名称
     *
     * @return
     */
    private String m_name;

    /**
     * 所属省
     */
    private int m_parentProvinceId;

    public int getM_id()
    {
        return m_id;
    }

    public void setM_id(int m_id)
    {
        this.m_id = m_id;
    }

    public int getM_code()
    {
        return m_code;
    }

    public void setM_code(int m_code)
    {
        this.m_code = m_code;
    }

    public String getM_name()
    {
        return m_name;
    }

    public void setM_name(String m_name)
    {
        this.m_name = m_name;
    }

    public int getM_parentProvinceId()
    {
        return m_parentProvinceId;
    }

    public void setM_parentProvinceId(int m_parentProvinceId)
    {
        this.m_parentProvinceId = m_parentProvinceId;
    }
}
