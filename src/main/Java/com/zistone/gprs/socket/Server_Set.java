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

    private ServerSocket _serverSocket;
    private Logger _logger = Logger.getLogger(Server_Set.class);
    //该线程是否正在运行
    private boolean _isRuning = false;
    private Thread _thread;
    private Server_GPRS server_gprs;
    private Server_MO server_mo;

    public Server_Set(Server_GPRS server_gprs, Server_MO server_mo) throws IOException
    {
        _serverSocket = new ServerSocket(PORT_SOCKET);
        this.server_gprs = server_gprs;
        this.server_mo = server_mo;
    }

    public void MyRun()
    {
        while (_isRuning)
        {
            try
            {
                Socket socket = _serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                String data = dataInputStream.readUTF();
                //例如:GPRS,300234067349750,020000000A040000000A0000000B0400000E10
                String deviceType = data.split(",")[0];
                switch (deviceType)
                {
                    case "GPRS":
                        server_gprs._setData = data;
                        _logger.debug(String.format(">>>收到来自Android端设置GPRS的参数:%s\r\n", data));
                        break;
                    case "YX":
                        server_mo._setData = data;
                        _logger.debug(String.format(">>>收到来自Android端设置铱星的参数:%s\r\n", data));
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
                _logger.error(String.format(">>>Set服务开启接收数据的线程时,发生异常:%s", e.getMessage()));
            }
        }
        _isRuning = false;
    }

    public void MyStart()
    {
        if (_isRuning)
        {
            _logger.error(">>>Set服务启动失败,该服务正在运行!");
        }
        else
        {
            _isRuning = true;
            _thread = new Thread(this::MyRun);
            _thread.setDaemon(true);
            _thread.start();
            _logger.debug(String.format(">>>Set服务的线程%d启动...\r\n", _thread.getId()));
        }
    }

    public void Join()
    {
        try
        {
            if (_thread != null)
            {
                _thread.join();
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void Stop()
    {
        _isRuning = false;
    }

}
