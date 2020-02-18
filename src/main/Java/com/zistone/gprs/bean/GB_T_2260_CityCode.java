package com.zistone.gprs.bean;

public class GB_T_2260_CityCode
{
    @Override
    public String toString()
    {
        return "GB_T_2260_CityCode{" +
                "id=" + id +
                ", code=" + code +
                ", name='" + name + '\'' +
                ", parentProvinceId=" + parentProvinceId +
                '}';
    }

    /**
     * 城市编号(手动生成)
     */
    private int id;

    /**
     * 城市编码
     */
    private int code;

    /**
     * 城市名称
     *
     * @return
     */
    private String name;

    /**
     * 所属省
     */
    private int parentProvinceId;

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

    public int getParentProvinceId()
    {
        return parentProvinceId;
    }

    public void setParentProvinceId(int parentProvinceId)
    {
        this.parentProvinceId = parentProvinceId;
    }

}
