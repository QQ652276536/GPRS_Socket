package com.zistone.socket;

import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.*;

public class Server_GPRS
{
    private static final int HEARTTIMEOUT_SOCKET;
    private static final int PORT_SOCKET;

    static
    {
        PORT_SOCKET = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET1"));
        HEARTTIMEOUT_SOCKET = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("HEARTTIMEOUT_SOCKET"));
    }

    private ServerSocket m_serverSocket;
    private Logger m_logger = Logger.getLogger(Server_GPRS.class);
    //该线程是否正在运行
    private boolean m_isRuning = false;
    private Thread m_thread;

    public Server_GPRS() throws IOException
    {
        m_serverSocket = new ServerSocket(PORT_SOCKET);
    }

    public void MyRun()
    {
        while (m_isRuning)
        {
            try
            {
                //开启监听
                Socket socket = m_serverSocket.accept();
                socket.setSoTimeout(HEARTTIMEOUT_SOCKET);
                Server_GPRS_Worker server_gprs_woker = new Server_GPRS_Worker(socket);
                //该线程用于接收GPRS数据
                Thread thread = new Thread(server_gprs_woker);
                thread.setDaemon(true);
                thread.start();
            }
            catch (Exception e)
            {
                m_logger.error(String.format(">>>MO服务开启接收数据的线程时,发生异常:%s", e.getMessage()));
                e.printStackTrace();
            }
        }
        m_isRuning = false;
    }

    public void MyStart()
    {
        if (m_isRuning)
        {
            m_logger.error(">>>GPRS服务(%s)启动失败,该服务正在运行!");
        }
        else
        {
            m_isRuning = true;
            //该线程用于设备并发连接
            m_thread = new Thread(this::MyRun);
            m_thread.setDaemon(true);
            m_thread.start();
            m_logger.debug(String.format(">>>GPRS服务的线程%d启动...", m_thread.getId()));
        }
    }

    public void Join()
    {
        try
        {
            if (m_thread != null)
            {
                m_thread.join();
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void Stop()
    {
        m_isRuning = false;
    }

}