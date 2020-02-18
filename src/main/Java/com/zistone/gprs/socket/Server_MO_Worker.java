package com.zistone.gprs.socket;

import com.zistone.gprs.message_type.MessageReceive_MO;
import com.zistone.gprs.util.ConvertUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class Server_MO_Worker implements Runnable
{
    private Socket _socket;
    private Logger _logger = Logger.getLogger(Server_MO_Worker.class);
    private String _clientIdentity;

    public Server_MO_Worker(Socket socket)
    {
        _socket = socket;
        InetSocketAddress inetSocketAddress = (InetSocketAddress) _socket.getRemoteSocketAddress();
        String clientIP = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();
        _clientIdentity = String.format("%s:%d", clientIP, clientPort);
    }

    public void Worker() throws Exception
    {
        MessageReceive_MO messageReceive_mo = new MessageReceive_MO();
        InputStream inputStream = _socket.getInputStream();
        OutputStream outputStream = _socket.getOutputStream();
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
                _logger.debug(String.format(">>>MO服务(%s)收到:%s", _clientIdentity, info));
                stringBuffer.delete(0, stringBuffer.length() - 1);
                //解析收到的内容并响应
                String responseStr = messageReceive_mo.RecevieHexStr(info);
                byte[] byteArray = ConvertUtil.HexStrToByteArray(responseStr);
                outputStream.write(byteArray);
                outputStream.flush();
                _logger.debug(String.format(">>>MO服务(%s)生成的响应内容:%s\r\n", _clientIdentity, responseStr));
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
            _logger.error(String.format(">>>连接MO服务(%s)的客户端断开:%s", _clientIdentity, e.getMessage()));
        }
        finally
        {
            try
            {
                _socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                _logger.error(String.format(">>>MO服务(%s)关闭Socket时发生异常:%s", _clientIdentity, e.getMessage()));
            }
        }
    }

}
