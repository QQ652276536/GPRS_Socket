package com.zistone.bean;

/**
 * 《中华人民共和国行政区划代码》的省级代码
 */
public class GB_T_2260_ProvinceCode
{
    public GB_T_2260_ProvinceCode()
    {
    }

    @Override
    public String toString()
    {
        return "GB_T_2260_ProvinceCode{" + "m_id=" + m_id + ", m_code=" + m_code + ", m_name='" + m_name + '\'' + '}';
    }

    /**
     * 省会编号(手动生成)
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
     * 省会编码
     */
    private int m_code;

    public int getM_code()
    {
        return m_code;
    }

    public void setM_code(int m_code)
    {
        this.m_code = m_code;
    }

    /**
     * 省会名称
     *
     * @return
     */
    private String m_name;

    public String getM_name()
    {
        return m_name;
    }

    public void setM_name(String m_name)
    {
        this.m_name = m_name;
    }
}
