package com.zistone.socket;

import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer
{
    private static int PORT_SOCKET;

    static
    {
        PORT_SOCKET = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET"));
    }

    private Logger m_logger = Logger.getLogger(SocketServer.class);

    public SocketServer()
    {
        try
        {
            m_logger.debug(">>>Socket服务启动,端口:" + PORT_SOCKET + ",等待终端连接...\r\n");
            ServerSocket server = new ServerSocket(PORT_SOCKET);
            int count = 0;
            while (true)
            {
                //开启监听
                Socket socket = server.accept();
                count++;
                m_logger.debug(">>>开启第" + count + "次长连接");
                ServerThread thread = new ServerThread(socket);
                thread.start();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
