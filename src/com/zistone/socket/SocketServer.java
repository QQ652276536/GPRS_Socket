package com.zistone.socket;

import org.apache.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer
{
    private static Logger LOG = Logger.getLogger(SocketServer.class);

    public SocketServer()
    {
        try
        {
            LOG.debug(">>>服务启动,等待终端的连接\n");
            ServerSocket server = new ServerSocket(8888);
            int count = 0;
            while (true)
            {
                //开启监听
                Socket socket = server.accept();
                count++;
                LOG.debug(">>>开启第" + count + "次长连接...");
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
