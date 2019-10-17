package com.zistone;

import com.zistone.socket.Server_GPRS;
import com.zistone.socket.Server_MO;
import com.zistone.socket.Server_MT_Worker;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class Main
{
    private static final int PORT_SOCKET;
    private static Logger m_logger = Logger.getLogger(Main.class);

    static
    {
        PORT_SOCKET = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET3"));
    }

    public static void main(String[] args) throws Exception
    {
        Server_GPRS server_gprs;
        Server_MO server_mo;
        try
        {
            server_gprs = new Server_GPRS();
            server_gprs.MyStart();
            server_mo = new Server_MO();
            server_mo.MyStart();

            ServerSocket serverSocket = new ServerSocket(PORT_SOCKET);
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            String data = dataInputStream.readUTF();
            //例如:GPRS&300234067349750&09,00,00&600
            m_logger.debug(">>>收到来自Android端的设置参数:" + data + "\r\n");
            String deviceType = data.split("&")[0];
            switch (deviceType)
            {
                case "GPRS":
                    server_gprs.m_data = data;
                    server_gprs.m_isSetGPRSParam = true;
                    break;
                case "YX":
                    server_mo.m_data = data;
                    server_mo.m_isSetYXParam = true;
                    break;
            }
            dataInputStream.close();
            socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        //挂起调用线程,被调用线程结束时才执行调用线程
        server_gprs.Join();
        server_mo.Join();

        server_gprs.Stop();
        server_mo.Stop();
    }
}
