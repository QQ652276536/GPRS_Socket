package com.zistone.gprs.socket;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketHttp
{
    private Logger _logger = Logger.getLogger(SocketHttp.class);

    private BufferedReader _bufferedReader;
    private BufferedWriter _bufferedWriter;
    private Socket _socket;

    /**
     * Post请求
     *
     * @param host IP地址
     * @param port 端口
     * @param path 路径
     * @param data 数据
     * @return
     */
    public String SendPost(String host, int port, String path, String data)
    {
        _socket = new Socket();
        try
        {
            //根据IP地址和端口号创建套接字地址
            SocketAddress address = new InetSocketAddress(host, port);
            _socket.connect(address);
            OutputStreamWriter streamWriter = new OutputStreamWriter(_socket.getOutputStream(), "UTF-8");
            _bufferedWriter = new BufferedWriter(streamWriter);
            //协议请求行
            _bufferedWriter.write("POST " + path + " HTTP/1.1\r\n");
            //协议请求头
            _bufferedWriter.write("Host: " + host + "\r\n");
            //经测试发现:如果是以JSON数据格式发送内容,这里使用getBytes().length来获取内容长度
            _bufferedWriter.write("Content-Length: " + data.getBytes("UTF-8").length + "\r\n");
            //JSON数据格式
            _bufferedWriter.write("Content-Type: application/json\r\n");
            //关键,换行,表示消息头结束,否则服务器会一直等待
            _bufferedWriter.write("\r\n");
            //发送的内容
            _bufferedWriter.write(data);
            _bufferedWriter.write("\r\n");
            _bufferedWriter.flush();
            //服务器的返回
            //字节流以UTF-8编码转换为字符流
            BufferedInputStream bufferedInputStream = new BufferedInputStream(_socket.getInputStream());
            _bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream, "UTF-8"));
            String line;
            String result = "";
            boolean isDebugResult = false;
            while ((line = _bufferedReader.readLine()) != null)
            {
                result += line;
                if (line.contains("{"))
                {
                    isDebugResult = true;
                }
                //TODO:如果Web服务没有返回实体,那么这里将不会执行,直到超时
                if (line.contains("}"))
                {
                    if (isDebugResult)
                    {
                        _logger.debug(line);
                    }
                    break;
                }
            }

            //           Stream<String> streams = _bufferedReader.lines();
            //           Object[] array = streams.toArray();
            //           int lineCount = array.length;
            //           for (int i = 0; i < lineCount; i++)
            //           {
            //               line = array[i].toString();
            //               _logger.debug(line);
            //               result += line;
            //           }
            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (null != _bufferedReader)
                    _bufferedReader.close();
                if (null != _bufferedWriter)
                    _bufferedWriter.close();
                if (null != _socket)
                    _socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                _logger.error(String.format(">>>本次请求发生异常:%s", e.getMessage()));
            }
        }
        return "Error...";
    }

}
