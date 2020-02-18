package com.zistone.gprs.bean;

public class GB_T_2260_AreaCode
{
    @Override
    public String toString()
    {
        return "GBT2260AreaCode{" +
                "id=" + id +
                ", code=" + code +
                ", name='" + name + '\'' +
                ", parentCityId=" + parentCityId +
                '}';
    }

    /**
     * 区域编号(手动生成)
     */
    private int id;

    /**
     * 区域编码
     */
    private int code;

    /**
     * 区域名称
     *
     * @return
     */
    private String name;

    /**
     * 所属市
     */
    private int parentCityId;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getParentCityId()
    {
        return parentCityId;
    }

    public void setParentCityId(int parentCityId)
    {
        this.parentCityId = parentCityId;
    }

}
