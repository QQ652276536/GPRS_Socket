package com.zistone.socket;

import com.zistone.message_type.MessageReceive_GPRS;
import com.zistone.util.ConvertUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Server_GPRS_Worker implements Runnable
{
    private Socket m_socket;
    private Logger m_logger = Logger.getLogger(Server_GPRS_Worker.class);
    private String m_clientIdentity;
    private int m_detail;
    private String m_setData;

    public Server_GPRS_Worker(Socket socket, int detail, String setData)
    {
        m_socket = socket;
        m_detail = detail;
        m_setData = setData;
        InetSocketAddress inetSocketAddress = (InetSocketAddress) m_socket.getRemoteSocketAddress();
        String clientIP = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();
        m_clientIdentity = String.format("%s:%d", clientIP, clientPort);
    }

    public void Worker() throws Exception
    {
        MessageReceive_GPRS messageReceive_gprs = new MessageReceive_GPRS();
        InputStream inputStream = m_socket.getInputStream();
        OutputStream outputStream = m_socket.getOutputStream();
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
                stringBuffer.delete(0, stringBuffer.length() - 1);
                //解析收到的内容并响应
                String responseStr = messageReceive_gprs.RecevieHexStr(info);
                //需要下发设置参数
                boolean tempFlag = false;
                if (responseStr.contains(","))
                {
                    String[] tempArray = responseStr.split(",");
                    responseStr = tempArray[0];
                    if (tempArray[1].equals("SETPARAM"))
                    {
                        tempFlag = true;
                    }
                }
                byte[] byteArray = ConvertUtil.HexStrToByteArray(responseStr);
                outputStream.write(byteArray);
                outputStream.flush();
                m_logger.debug(String.format(">>>GPRS服务(%s)生成的响应内容:%s\r\n", m_clientIdentity, responseStr));
                //鉴权完毕后发送参数设置
                if (tempFlag)
                {
                    //延时发送
                    Thread.sleep(100);
                    if (m_setData != null && !m_setData.equals(""))
                    {
                        String hexDetail = ConvertUtil.IntToHexStr(m_detail);
                        new SendParamSetting(m_socket, m_setData).SendToGPRS(hexDetail);
                    }
                }
            }
        }
    }

    @Override
    public void run()
    {
        try
        {
            Worker();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            m_logger.error(String.format(">>>连接GPRS服务(%s)的客户端断开:%s", m_clientIdentity, e.getMessage()));
        }
        finally
        {
            try
            {
                m_socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                m_logger.error(String.format(">>>MO服务(%s)关闭Socket时发生异常:%s", m_clientIdentity, e.getMessage()));
            }
        }
    }

}
