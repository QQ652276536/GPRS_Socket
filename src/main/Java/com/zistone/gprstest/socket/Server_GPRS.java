package com.zistone.gprstest.socket;

import com.zistone.gprstest.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_GPRS
{
    private static final int HEARTTIMEOUT_SOCKET;
    private static final int PORT_SOCKET;
    private static int m_detail = 6550;

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
    public String m_setData = "";

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
                Socket socket = m_serverSocket.accept();
                socket.setSoTimeout(HEARTTIMEOUT_SOCKET);
                //在下发参数设置前有一次设备鉴权后的通用应答
                m_detail += 2;
                Server_GPRS_Worker server_gprs_woker = new Server_GPRS_Worker(socket, m_detail, m_setData);
                Thread thread = new Thread(server_gprs_woker);
                thread.setDaemon(true);
                thread.start();
                m_logger.debug(">>>----------Server_GPRS_Worker线程启动----------");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                m_logger.error(">>>GPRS服务开启接收数据的线程时,发生异常:" + e.getMessage());
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
