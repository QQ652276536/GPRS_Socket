package com.zistone.gprs.socket;

import com.zistone.gprs.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_Set
{
    private static final int PORT_SOCKET;

    static
    {
        PORT_SOCKET = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET3"));
    }

    private ServerSocket m_serverSocket;
    private Logger m_logger = Logger.getLogger(Server_Set.class);
    //该线程是否正在运行
    private boolean m_isRuning = false;
    private Thread m_thread;
    private Server_GPRS server_gprs;
    private Server_MO server_mo;

    public Server_Set(Server_GPRS server_gprs,Server_MO server_mo) throws IOException
    {
        m_serverSocket = new ServerSocket(PORT_SOCKET);
        this.server_gprs = server_gprs;
        this.server_mo = server_mo;
    }

    public void MyRun()
    {
        while (m_isRuning)
        {
            try
            {
                Socket socket = m_serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                String data = dataInputStream.readUTF();
                //例如:GPRS,300234067349750,020000000A040000000A0000000B0400000E10
                String deviceType = data.split(",")[0];
                switch (deviceType)
                {
                    case "GPRS":
                        server_gprs.m_setData = data;
                        m_logger.debug(">>>收到来自Android端设置GPRS的参数:" + data + "\r\n");
                        break;
                    case "YX":
                        server_mo.m_setData = data;
                        m_logger.debug(">>>收到来自Android端设置铱星的参数:" + data + "\r\n");
                        break;
                }
                dataOutputStream.writeUTF("OK");
                dataOutputStream.flush();
                dataInputStream.close();
                dataOutputStream.close();
                socket.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                m_logger.error(">>>Set服务开启接收数据的线程时,发生异常:%s" + e.getMessage());
            }
        }
        m_isRuning = false;
    }

    public void MyStart()
    {
        if (m_isRuning)
        {
            m_logger.error(">>>Set服务(%s)启动失败,该服务正在运行!");
        }
        else
        {
            m_isRuning = true;
            m_thread = new Thread(this::MyRun);
            m_thread.setDaemon(true);
            m_thread.start();
            m_logger.debug(String.format(">>>Set服务的线程%d启动...\r\n", m_thread.getId()));
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
