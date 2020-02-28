package com.zistone.gprs.socket;

import com.zistone.gprs.util.MyConvertUtil;
import com.zistone.gprs.util.MyPropertiesUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.*;

public class Server_MO
{
    private static final int HEARTTIMEOUT_SOCKET;
    private static final int PORT_SOCKET_MO;
    private static String _yxIP;
    private static int _mtPort;
    private static int _detail = 6550;

    static
    {
        HEARTTIMEOUT_SOCKET = Integer.valueOf(MyPropertiesUtil.GetValueProperties().getProperty("HEARTTIMEOUT_SOCKET"));
        _yxIP = MyPropertiesUtil.GetValueProperties().getProperty("IP_YX");
        PORT_SOCKET_MO = Integer.valueOf(MyPropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET2"));
        _mtPort = Integer.valueOf(MyPropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET4"));
    }

    private Logger _logger = Logger.getLogger(Server_MO.class);
    private ServerSocket _serverSocket;
    //该线程是否正在运行
    private boolean _isRuning = false;
    private Thread _thread;
    public String _setData = "";

    public Server_MO() throws IOException
    {
        _serverSocket = new ServerSocket(PORT_SOCKET_MO);
    }

    public void MyRun()
    {
        while (_isRuning)
        {
            try
            {
                Socket socket = _serverSocket.accept();
                socket.setSoTimeout(HEARTTIMEOUT_SOCKET);
                //先发送MT,再接收数据
                if (_setData != null && !_setData.equals(""))
                {
                    Socket tempSocket = new Socket(_yxIP, _mtPort);
                    _detail += 1;
                    String hexDetail = MyConvertUtil.IntToHexStr(_detail);
                    new SendParamSetting(tempSocket, _setData).SendToMT(hexDetail);
                    tempSocket.close();
                    Thread.sleep(100);
                }
                Server_MO_Worker server_mo_woker = new Server_MO_Worker(socket);
                Thread thread = new Thread(server_mo_woker);
                thread.setDaemon(true);
                thread.start();
                _logger.debug(">>>----------Server_MO_Worker线程启动----------");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                _logger.error(String.format(">>>MO服务开启接收数据的线程时,发生异常:%s", e.getMessage()));
            }
        }
        _isRuning = false;
    }

    public void MyStart()
    {
        if (_isRuning)
        {
            _logger.error(">>>MO服务启动失败,该服务正在运行!");
        }
        else
        {
            _isRuning = true;
            _thread = new Thread(this::MyRun);
            _thread.setDaemon(true);
            _thread.start();
            _logger.debug(String.format(">>>MO服务的线程%d启动...", _thread.getId()));
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
