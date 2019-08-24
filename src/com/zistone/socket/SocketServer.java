package com.zistone.socket;

import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer
{
    private static Logger LOG = Logger.getLogger(SocketServer.class);

    private static String SERVERIP;
    private static int SERVERPORT;
    private static int SOCKETPORT;

    static
    {
        SERVERIP = PropertiesUtil.GetValueProperties().getProperty("SERVERIP");
        SERVERPORT = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("SERVERPORT"));
        SOCKETPORT = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("SOCKETPORT"));
    }

    public SocketServer()
    {
        try
        {
            LOG.debug(">>>服务启动,Socket端口:" + SOCKETPORT + "服务地址:" + SERVERIP + "服务端口:" + SERVERPORT + ",等待终端连接...\n");
            ServerSocket server = new ServerSocket(SOCKETPORT);
            int count = 0;
            while (true)
            {
                //开启监听
                Socket socket = server.accept();
                count++;
                LOG.debug(">>>开启第" + count + "次长连接");
                ServerThread thread = new ServerThread(socket, SERVERIP, SERVERPORT);
                thread.start();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
