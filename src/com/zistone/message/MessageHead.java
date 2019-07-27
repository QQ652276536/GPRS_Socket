package com.zistone.message;

import java.util.Arrays;

public class MessageHead
{
    @Override public String toString()
    {
        return "MessageHead{" + "m_id=" + Arrays.toString(m_id) + ", m_messageBody=" + Arrays.toString(m_messageBody) + ", m_phone=" + Arrays.toString(m_phone) + ", m_detail=" + Arrays.toString(m_detail) + ", m_package=" + Arrays.toString(m_package) + '}';
    }

    /**
     * 消息ID
     * 2个无符号双字节整型
     */
    private byte[] m_id;

    public byte[] getM_id()
    {
        return m_id;
    }

    public void setM_id(byte[] m_id)
    {
        this.m_id = m_id;
    }

    /**
     * 消息体属性
     * 2个无符号双字节整型
     */
    private byte[] m_messageBody;

    public byte[] getM_messageBody()
    {
        return m_messageBody;
    }

    public void setM_messageBody(byte[] m_messageBody)
    {
        this.m_messageBody = m_messageBody;
    }

    /**
     * 终端手机号
     * 6个8421码
     */
    private byte[] m_phone;

    public byte[] getM_phone()
    {
        return m_phone;
    }

    public void setM_phone(byte[] m_phone)
    {
        this.m_phone = m_phone;
    }

    /**
     * 消息流水号
     * 2个无符号双字节整型
     */
    private byte[] m_detail;

    public byte[] getM_detail()
    {
        return m_detail;
    }

    public void setM_detail(byte[] m_detail)
    {
        this.m_detail = m_detail;
    }

    /**
     * 消息包封装项
     */
    private byte[] m_package;

    public byte[] getM_package()
    {
        return m_package;
    }

    public void setM_package(byte[] m_package)
    {
        this.m_package = m_package;
    }
}
