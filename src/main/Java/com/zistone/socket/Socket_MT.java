package com.zistone.socket;

import com.zistone.util.ConvertUtil;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;

public class Socket_MT
{
    private static SimpleDateFormat SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String YXGATEWAY_IP;
    private static int PORT_SOCKET;

    static
    {
        YXGATEWAY_IP = PropertiesUtil.GetValueProperties().getProperty("YXGATEWAY_IP");
        PORT_SOCKET = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET4"));
    }

    private Socket m_socket;
    private Logger m_logger = Logger.getLogger(Socket_MT.class);
    private String m_clientIdentity;

    public Socket_MT() throws IOException
    {
        m_socket = new Socket(YXGATEWAY_IP, PORT_SOCKET);
        InetSocketAddress inetSocketAddress = (InetSocketAddress) m_socket.getRemoteSocketAddress();
        String clientIP = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();
        m_clientIdentity = String.format("%s:%d", clientIP, clientPort);
    }

    public String SendData(String hexStr)
    {
        String info = "";
        try
        {
            byte[] byteArray = ConvertUtil.HexStrToByteArray(hexStr);
            OutputStream outputStream = m_socket.getOutputStream();
            outputStream.write(byteArray);
            //刷新缓冲
            outputStream.flush();
            //得到一个输入流，用于接收服务器响应的数据
            InputStream inputStream = m_socket.getInputStream();
            //一次读取一个byte
            byte[] bytes = new byte[1];
            while (true)
            {
                if (inputStream.read(bytes) <= 0)
                {
                    break;
                }
                inputStream.read(bytes);
                info += bytes;
                //已经读完
                if (inputStream.available() == 0)
                {
                    m_logger.debug(String.format(">>>MT服务(%s)收到来自铱星网关的信息:%s", m_clientIdentity, info));
                    break;
                }
            }
        }
        catch (Exception e)
        {
            m_logger.error(String.format(">>>MT服务开启接收数据的线程时,发生异常:%s", e.getMessage()));
            e.printStackTrace();
        }
        return info;
    }

}
