package com.zistone.socket;

import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.*;

public class Server_MO
{
    private static final int HEARTTIMEOUT_SOCKET;
    private static final int PORT_SOCKET_MO;
    private static String YXGATEWAY_IP;
    private static int PORT_SOCKET_MT;

    static
    {
        HEARTTIMEOUT_SOCKET = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("HEARTTIMEOUT_SOCKET"));
        YXGATEWAY_IP = PropertiesUtil.GetValueProperties().getProperty("YXGATEWAY_IP");
        PORT_SOCKET_MO = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET2"));
        PORT_SOCKET_MT = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET4"));
    }

    private ServerSocket m_serverSocket;
    private Logger m_logger = Logger.getLogger(Server_MO.class);
    //该线程是否正在运行
    private boolean m_isRuning = false;
    private Thread m_thread;
    public String m_data = "YX&300234067349750&09,00,00&600";

    public Server_MO() throws IOException
    {
        m_serverSocket = new ServerSocket(PORT_SOCKET_MO);
    }

    public void MyRun()
    {
        while (m_isRuning)
        {
            try
            {
                Socket socket = m_serverSocket.accept();
                socket.setSoTimeout(HEARTTIMEOUT_SOCKET);
                //先发送MT,再接收数据
                if (m_data != null && !m_data.equals(""))
                {
                    Socket tempSocket = new Socket(YXGATEWAY_IP, PORT_SOCKET_MT);
                    new SendParamSetting(tempSocket, m_data).SendMT();
                }
                Server_MO_Worker server_mo_woker = new Server_MO_Worker(socket);
                Thread thread = new Thread(server_mo_woker);
                thread.setDaemon(true);
                thread.start();
                m_logger.debug(">>>----------Server_MO_Worker线程启动----------");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                m_logger.error(">>>MO服务开启接收数据的线程时,发生异常:%s" + e.getMessage());
            }
        }
        m_isRuning = false;
    }

    public void MyStart()
    {
        if (m_isRuning)
        {
            m_logger.error(">>>MO服务(%s)启动失败,该服务正在运行!");
        }
        else
        {
            m_isRuning = true;
            m_thread = new Thread(this::MyRun);
            m_thread.setDaemon(true);
            m_thread.start();
            m_logger.debug(String.format(">>>MO服务的线程%d启动...", m_thread.getId()));
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
