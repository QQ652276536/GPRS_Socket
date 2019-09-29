package com.zistone;

import com.zistone.socket.Server_GPRS;
import com.zistone.socket.Server_MO;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;

public class Main
{
    private static int PORT_SOCKET_GPRS;
    private static int PORT_SOCKET_MO;

    static
    {
        PORT_SOCKET_GPRS = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET1"));
        PORT_SOCKET_MO = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET2"));
    }

    private static Logger m_logger = Logger.getLogger(Main.class);

    public static void main(String[] args)
    {
        try
        {
            ServerSocket server_gprs = new ServerSocket(PORT_SOCKET_GPRS);
            m_logger.debug(">>>服务启动,等待终端连接...\r\n");
            while (true)
            {
                //开启监听
                Socket socket_gprs = server_gprs.accept();
                Server_GPRS server_gprs_thread = new Server_GPRS(socket_gprs);
                server_gprs_thread.start();

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
