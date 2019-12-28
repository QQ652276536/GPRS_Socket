package com.zistone.gprs.socket;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Server_MT_Worker implements Runnable
{
    private Socket m_socket;
    private Logger m_logger = Logger.getLogger(Server_MT_Worker.class);
    private String m_clientIdentity;

    public Server_MT_Worker(Socket socket)
    {
        m_socket = socket;
        InetSocketAddress inetSocketAddress = (InetSocketAddress) m_socket.getRemoteSocketAddress();
        String clientIP = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();
        m_clientIdentity = String.format("%s:%d", clientIP, clientPort);
    }

    public void Worker() throws Exception
    {
        InputStream inputStream = m_socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        OutputStream outputStream = m_socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        String data = dataInputStream.readUTF();
        m_logger.debug(String.format(">>>MT服务(%s)收到来自客户端的终端设置参数:%s", m_clientIdentity, data));
        //TODO:读取客户端发送的数据并发送至铱星网关
        dataOutputStream.close();
        dataInputStream.close();
    }

    @Override
    public void run()
    {
        try
        {
            Worker();
        }
        catch (SocketTimeoutException e)
        {
            e.printStackTrace();
            m_logger.error(String.format(">>>MT服务(%s)读取超时:%s", m_clientIdentity, e.getMessage()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            m_logger.error(String.format(">>>连接MT服务(%s)的客户端断开:%s", m_clientIdentity, e.getMessage()));
        }
        try
        {
            m_socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            m_logger.error(String.format(">>>MT服务(%s)关闭Socket时发生异常:%s", m_clientIdentity, e.getMessage()));
        }
    }

}
