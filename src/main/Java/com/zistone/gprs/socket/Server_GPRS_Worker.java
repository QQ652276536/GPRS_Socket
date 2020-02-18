package com.zistone.gprs.socket;

import com.zistone.gprs.message_type.MessageReceive_GPRS;
import com.zistone.gprs.util.ConvertUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Server_GPRS_Worker implements Runnable
{
    private Logger _logger = Logger.getLogger(Server_GPRS_Worker.class);
    private Socket _socket;
    private String _clientIdentity;
    private String _setData;
    private int _detail;

    public Server_GPRS_Worker(Socket socket, int detail, String setData)
    {
        _socket = socket;
        _detail = detail;
        _setData = setData;
        InetSocketAddress inetSocketAddress = (InetSocketAddress) _socket.getRemoteSocketAddress();
        String clientIP = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();
        _clientIdentity = String.format("%s:%d", clientIP, clientPort);
    }

    public void Worker() throws Exception
    {
        MessageReceive_GPRS messageReceive_gprs = new MessageReceive_GPRS();
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
                _logger.debug(String.format(">>>GPRS服务(%s)收到:%s", _clientIdentity, info));
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
                _logger.debug(String.format(">>>GPRS服务(%s)生成的响应内容:%s\r\n", _clientIdentity, responseStr));
                //鉴权完毕后发送参数设置
                if (tempFlag)
                {
                    //延时发送
                    Thread.sleep(100);
                    if (_setData != null && !_setData.equals(""))
                    {
                        String hexDetail = ConvertUtil.IntToHexStr(_detail);
                        new SendParamSetting(_socket, _setData).SendToGPRS(hexDetail);
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
            _logger.error(String.format(">>>连接GPRS服务(%s)的客户端断开:%s", _clientIdentity, e.getMessage()));
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
