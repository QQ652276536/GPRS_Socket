package com.zistone.bean;

public class MessageBody
{
    /**
     * 消息体长度
     * 长度10
     */
    private int m_messageLength;

    public int getM_messageLength()
    {
        return m_messageLength;
    }

    public void setM_messageLength(int m_messageLength)
    {
        this.m_messageLength = m_messageLength;
    }

    /**
     * 加密方式
     * 长度3,第一位为1时表示经过RSA算法加密,都为0时表示不加密
     */
    private String m_encryptionType;

    public String getM_encryptionType()
    {
        return m_encryptionType;
    }

    public void setM_encryptionType(String m_encryptionType)
    {
        this.m_encryptionType = m_encryptionType;
    }

    /**
     * 分包
     * 当消息体属性中第十三位为1时表示消息体为长消息,进行分包发送处理,具体分包信息由消息包封装项决定;若第十三位为0,则消息头中无消息包封装项字段。
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
