package com.zistone.gprs.bean;

/**
 * 《中华人民共和国行政区划代码》的区域级代码
 */
public class GB_T_2260_AreaCode
{
    @Override
    public String toString()
    {
        return "GB_T_2260_AreaCode{" + "m_id=" + m_id + ", m_code=" + m_code + ", m_name='" + m_name + '\'' + ", m_parentCityId=" + m_parentCityId + '}';
    }

    /**
     * 区域编号(手动生成)
     */
    private int m_id;

    /**
     * 区域编码
     */
    private int m_code;

    /**
     * 区域名称
     *
     * @return
     */
    private String m_name;

    /**
     * 所属市
     */
    private int m_parentCityId;

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

    public int getM_parentCityId()
    {
        return m_parentCityId;
    }

    public void setM_parentCityId(int m_parentCityId)
    {
        this.m_parentCityId = m_parentCityId;
    }
}
