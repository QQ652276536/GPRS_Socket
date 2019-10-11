package com.zistone.socket;

import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_MT
{
    private static final int HEARTTIMEOUT_SOCKET;
    private static final int PORT_SOCKET;

    static
    {
        PORT_SOCKET = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET3"));
        HEARTTIMEOUT_SOCKET = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("HEARTTIMEOUT_SOCKET"));
    }

    private ServerSocket m_serverSocket;
    private Logger m_logger = Logger.getLogger(Server_MT.class);
    //该线程是否正在运行
    private boolean m_isRuning = false;
    private Thread m_thread;

    public Server_MT() throws IOException
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
                Server_MT_Worker server_mt_woker = new Server_MT_Worker(socket);
                //该线程用于接收MT数据
                Thread thread = new Thread(server_mt_woker);
                thread.setDaemon(true);
                thread.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                m_logger.error(">>>MT服务开启接收数据的线程时,发生异常:%s" + e.getMessage());
            }
        }
        m_isRuning = false;
    }

    public void MyStart()
    {
        if (m_isRuning)
        {
            m_logger.error(">>>MT服务(%s)启动失败,该服务正在运行!");
        }
        else
        {
            m_isRuning = true;
            //该线程用于设备并发连接
            m_thread = new Thread(this::MyRun);
            m_thread.setDaemon(true);
            m_thread.start();
            m_logger.debug(String.format(">>>MT服务的线程%d启动...", m_thread.getId()));
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
