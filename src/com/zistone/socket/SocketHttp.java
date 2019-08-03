package com.zistone.socket;

import com.zistone.bean.DeviceInfo;
import org.json.JSONObject;
import org.json.JSONPointerException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketHttp
{
    private BufferedReader m_bufferedReader;
    private BufferedWriter m_bufferedWriter;
    private Socket m_socket;

    public static void main(String[] args)
    {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setM_deviceName("Socket");
        deviceInfo.setM_type("SSS");
        deviceInfo.setM_lat(1.111);
        deviceInfo.setM_lot(2.222);
        deviceInfo.setM_description("我是Socket模拟的Http请求发送过来的");
        JSONObject jsonObject = new JSONObject(deviceInfo);
        new SocketHttp().SendPost("localhost", 8080, "/Blowdown_Web/DeviceInfo/Insert", jsonObject);
    }

    /**
     * Post请求
     *
     * @param host       IP地址
     * @param port       端口
     * @param path       路径
     * @param jsonObject 内容
     * @return
     */
    public String SendPost(String host, int port, String path, JSONObject jsonObject)
    {
        String data;
        if (null != host && !"".equals(host) && 0 != port && null != path && !"".equals(path) && null != jsonObject)
        {
            m_socket = new Socket();
            data = jsonObject.toString();
            try
            {
                //根据IP地址和端口号创建套接字地址
                SocketAddress address = new InetSocketAddress(host, port);
                m_socket.connect(address);
                OutputStreamWriter streamWriter = new OutputStreamWriter(m_socket.getOutputStream(), "UTF-8");
                m_bufferedWriter = new BufferedWriter(streamWriter);
                //协议请求行
                m_bufferedWriter.write("POST " + path + " HTTP/1.1\r\n");
                //协议请求头
                m_bufferedWriter.write("Host: " + host + "\r\n");
                //经测试发现:如果是以JSON数据格式发送内容,这里使用getBytes().length来获取内容长度
                m_bufferedWriter.write("Content-Length: " + data.getBytes("UTF-8").length + "\r\n");
                //JSON数据格式
                m_bufferedWriter.write("Content-Type: application/json\r\n");
                //关键,换行,表示消息头结束,否则服务器会一直等待
                m_bufferedWriter.write("\r\n");
                //发送的内容
                m_bufferedWriter.write(data);
                m_bufferedWriter.write("\r\n");
                m_bufferedWriter.flush();
                //服务器的返回
                //二进制字节流以UTF-8编码转换为字符流
                BufferedInputStream bufferedInputStream = new BufferedInputStream(m_socket.getInputStream());
                m_bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream, "UTF-8"));
                String line;
                while ((line = m_bufferedReader.readLine()) != null)
                {
                    System.out.println(line);
                    if (line.contains("<<<"))
                    {
                        return line.replace("<<<", "");
                    }
                }
            }
            catch (IOException | JSONPointerException e)
            {
                e.printStackTrace();
                return "Exception...";
            }
            finally
            {
                try
                {
                    if (null != m_bufferedReader)
                        m_bufferedReader.close();
                    if (null != m_bufferedWriter)
                        m_bufferedWriter.close();
                    if (null != m_socket)
                        m_socket.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return "ParamError...";
    }

}
