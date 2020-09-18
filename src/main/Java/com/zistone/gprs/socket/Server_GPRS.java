package com.zistone.gprs.socket;

import com.zistone.gprs.util.MyPropertiesUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_GPRS
{
    private static final int HEARTTIMEOUT_SOCKET;
    private static final int PORT_SOCKET;
    private static int _detail = 6550;

    static
    {
        PORT_SOCKET = Integer.parseInt(MyPropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET1"));
        HEARTTIMEOUT_SOCKET = Integer.parseInt(MyPropertiesUtil.GetValueProperties().getProperty("HEARTTIMEOUT_SOCKET"));
    }

    private Logger _logger = Logger.getLogger(Server_GPRS.class);
    private ServerSocket _serverSocket;
    //该线程是否正在运行
    private boolean _isRuning = false;
    private Thread _thread;
    public String _setData = "";

    public Server_GPRS() throws IOException
    {
        _serverSocket = new ServerSocket(PORT_SOCKET);
    }

    public void MyRun()
    {
        while (_isRuning)
        {
            try
            {
                Socket socket = _serverSocket.accept();
                socket.setSoTimeout(HEARTTIMEOUT_SOCKET);
                //在下发参数设置前有一次设备鉴权后的通用应答
                _detail += 2;
                Server_GPRS_Worker server_gprs_woker = new Server_GPRS_Worker(socket, _detail, _setData);
                Thread thread = new Thread(server_gprs_woker);
                thread.setDaemon(true);
                thread.start();
                _logger.debug(">>>----------Server_GPRS_Worker线程启动----------");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                _logger.error(String.format(">>>GPRS服务开启接收数据的线程时,发生异常:%s", e.getMessage()));
            }
        }
        _isRuning = false;
    }

    public void MyStart()
    {
        if (_isRuning)
        {
            _logger.error(">>>GPRS服务启动失败,该服务正在运行!");
        }
        else
        {
            _isRuning = true;
            _thread = new Thread(this::MyRun);
            _thread.setDaemon(true);
            _thread.start();
            _logger.debug(String.format(">>>GPRS服务的线程%d启动...", _thread.getId()));
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
