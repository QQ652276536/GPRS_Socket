package com.zistone.gprs.bean;

public class GB_T_2260_ProvinceCode
{
    @Override
    public String toString()
    {
        return "GBT2260ProvinceCode{" + "id=" + id + ", code=" + code + ", name='" + name + '\'' + '}';
    }

    /**
     * 省会编号(手动生成)
     */
    private int id;

    /**
     * 省会编码
     */
    private int code;

    /**
     * 省会名称
     *
     * @return
     */
    private String name;

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

}
