package com.zistone.socket;

import com.zistone.message_type.MessageReceive_GPRS;
import com.zistone.message_type.MessageReceive_GPRS;
import com.zistone.util.ConvertUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class Server_GPRS_Worker implements Runnable
{
    private Socket m_socket;
    private Logger m_logger = Logger.getLogger(Server_GPRS_Worker.class);
    private String m_clientIdentity;

    public Server_GPRS_Worker(Socket socket)
    {
        m_socket = socket;
        InetSocketAddress inetSocketAddress = (InetSocketAddress) m_socket.getRemoteSocketAddress();
        String clientIP = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();
        m_clientIdentity = String.format("%s:%d", clientIP, clientPort);
    }

    public void MyRun() throws IOException
    {
        MessageReceive_GPRS messageReceive_gprs = new MessageReceive_GPRS();
        //字节输入流
        InputStream inputStream = m_socket.getInputStream();
        //字节输出流
        OutputStream outputStream = m_socket.getOutputStream();
        //按byte读
        byte[] bytes = new byte[1];
        StringBuffer stringBuffer = new StringBuffer();
        while (true)
        {
            if (inputStream.read(bytes) <= 0)
            {
                break;
            }
            String tempStr = ConvertUtil.ByteArrayToHexStr(bytes) + ",";
            stringBuffer.append(tempStr);
            //返回下次调用可以不受阻塞地从此流读取或跳过的估计字节数,如果等于0则表示已经读完
            if (inputStream.available() == 0)
            {
                String info = stringBuffer.toString();
                m_logger.debug(String.format(">>>GPRS服务(%s)收到:%s", m_clientIdentity, info));
                //清空缓冲
                stringBuffer.delete(0, stringBuffer.length() - 1);
                //解析收到的内容并响应
                String responseStr = messageReceive_gprs.RecevieHexStr(info);
                byte[] byteArray = ConvertUtil.HexStrToByteArray(responseStr);
                outputStream.write(byteArray);
                outputStream.flush();
                m_logger.debug(String.format(">>>GPRS服务(%s)生成的响应内容:%s", m_clientIdentity, responseStr));
            }
        }
    }

    @Override
    public void run()
    {
        try
        {
            MyRun();
        }
        catch (IOException e)
        {
            m_logger.error(String.format(">>>连接GPRS服务(%s)的客户端断开:%s", m_clientIdentity, e.getMessage()));
            e.printStackTrace();
        }
        try
        {
            m_socket.close();
        }
        catch (IOException e)
        {
            m_logger.error(String.format(">>>GPRS服务(%s)关闭Socket时发生异常:%s", m_clientIdentity, e.getMessage()));
            e.printStackTrace();
        }
    }

}
