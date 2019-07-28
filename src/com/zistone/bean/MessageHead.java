package com.zistone.bean;

public class MessageHead
{

    /**
     * 消息ID
     * 2个无符号双字节整型
     */
    private String m_id;

    public String getM_id()
    {
        return m_id;
    }

    public void setM_id(String m_id)
    {
        this.m_id = m_id;
    }

    /**
     * 消息体属性
     * 2个无符号双字节整型
     */
    private MessageBody m_messageBody;

    public MessageBody getM_messageBody()
    {
        return m_messageBody;
    }

    public void setM_messageBody(MessageBody m_messageBody)
    {
        this.m_messageBody = m_messageBody;
    }

    /**
     * 终端手机号
     * 6个8421码
     */
    private String m_phone;

    public String getM_phone()
    {
        return m_phone;
    }

    public void setM_phone(String m_phone)
    {
        this.m_phone = m_phone;
    }

    /**
     * 消息流水号
     * 2个无符号双字节整型
     */
    private String m_detail;

    public String getM_detail()
    {
        return m_detail;
    }

    public void setM_detail(String m_detail)
    {
        this.m_detail = m_detail;
    }

    /**
     * 消息包封装项
     */
    private String m_package;

    public String getM_package()
    {
        return m_package;
    }

    public void setM_package(String m_package)
    {
        this.m_package = m_package;
    }
}
