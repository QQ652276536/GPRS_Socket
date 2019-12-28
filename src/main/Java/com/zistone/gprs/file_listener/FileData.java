package com.zistone.gprs.file_listener;

public class FileData
{
    @Override
    public String toString()
    {
        return "FileData{" + "m_path='" + m_path + '\'' + ", m_encode='" + m_encode + '\'' + ", m_time=" + m_time + '}';
    }

    private String m_path;
    private String m_encode;
    private String m_content;
    private int m_time;

    public String getM_path()
    {
        return m_path;
    }

    public void setM_path(String m_path)
    {
        this.m_path = m_path;
    }

    public String getM_encode()
    {
        return m_encode;
    }

    public void setM_encode(String m_encode)
    {
        this.m_encode = m_encode;
    }

    public String getM_content()
    {
        return m_content;
    }

    public void setM_content(String m_content)
    {
        this.m_content = m_content;
    }

    public int getM_time()
    {
        return m_time;
    }

    public void setM_time(int m_time)
    {
        this.m_time = m_time;
    }
}
