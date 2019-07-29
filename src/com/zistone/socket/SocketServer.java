package com.zistone.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer
{

    public SocketServer(String content)
    {
        try
        {
            System.out.println("_____________________服务启动_____________________");
            ServerSocket server = new ServerSocket(8888);
            while (true)
            {
                //开启监听
                Socket socket = server.accept();
                ServerThread thread = new ServerThread(socket, content);
                thread.start();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
