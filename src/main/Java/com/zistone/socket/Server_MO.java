package com.zistone.socket;

import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.*;

public class Server_MO
{
    private static final int PORT_SOCKET;

    static
    {
        PORT_SOCKET = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET2"));
    }

    private ServerSocket m_serverSocket;
    private Logger m_logger = Logger.getLogger(Server_MO.class);
    //该线程是否正在运行
    private boolean m_isRuning = false;
    private Thread m_thread;
    private boolean m_isSetParam = true;

    public Server_MO() throws IOException
    {
        m_serverSocket = new ServerSocket(PORT_SOCKET);
    }

    private boolean bbb = true;
    public void MyRun()
    {
        while (m_isRuning)
        {
            try
            {
                //开启监听
                Socket socket = m_serverSocket.accept();
                //先配置MT,再接收数据
                if (bbb)
                {
                    bbb = false;
                    m_logger.debug(String.format(">>>>>>>>>>>>>>>>>>>>执行发送参数设置<<<<<<<<<<<<<<<<<<<<"));
                    new Socket_MT().SendData("300234067349750◎REPORTINTERVAL◎STARTTIME◎1,1,1");
                }
                socket.setSoTimeout(30 * 1000);
                Server_MO_Worker server_mo_woker = new Server_MO_Worker(socket);
                //该线程用于接收MO数据
                Thread thread = new Thread(server_mo_woker);
                thread.setDaemon(true);
                thread.start();
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
            //该线程用于设备并发连接
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
