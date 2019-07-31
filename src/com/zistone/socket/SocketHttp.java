package com.zistone.socket;

import com.zistone.bean.DeviceInfo;
import org.json.JSONObject;
import org.json.JSONPointerException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URLEncoder;

public class SocketHttp
{
    private int m_port;
    private String m_host;
    private Socket m_socket;
    private BufferedReader m_bufferedReader;
    private BufferedWriter m_bufferedWriter;
    private String m_path;
    private String m_data;

    public static void main(String[] args)
    {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setM_deviceName("Socket");
        deviceInfo.setM_type("SSS");
        deviceInfo.setM_lat(1.111);
        deviceInfo.setM_lot(2.222);
        deviceInfo.setM_description("我是Socket模拟的Http请求发送过来的");
        JSONObject jsonObject = new JSONObject(deviceInfo);
        new SocketHttp("localhost", 8080, "/Blowdown_Web/DeviceInfo/Insert", jsonObject);
    }

    public SocketHttp(String host, int port, String path, JSONObject jsonObject)
    {
        if (null != host && !"".equals(host) && 0 != port && null != path && !"".equals(path) && null != jsonObject)
        {
            m_socket = new Socket();
            m_host = host;
            m_port = port;
            m_path = path;
            m_data = jsonObject.toString();
            SendPost();
        }
    }

    public String SendPost()
    {
        String result = null;
        try
        {
            //根据IP地址和端口号创建套接字地址
            SocketAddress address = new InetSocketAddress(m_host, m_port);
            m_socket.connect(address);
            OutputStreamWriter streamWriter = new OutputStreamWriter(m_socket.getOutputStream(), "UTF-8");
            m_bufferedWriter = new BufferedWriter(streamWriter);
            //协议请求行
            m_bufferedWriter.write("POST " + m_path + " HTTP/1.1\r\n");
            //协议请求头
            m_bufferedWriter.write("Host: " + m_host + "\r\n");
            //经测试发现:如果是以JSON数据格式发送内容,这里使用getBytes().length来获取内容长度
            m_bufferedWriter.write("Content-Length: " + m_data.getBytes("UTF-8").length + "\r\n");
            //JSON数据格式
            m_bufferedWriter.write("Content-Type: application/json\r\n");
            //关键,换行,表示消息头结束,否则服务器会一直等待
            m_bufferedWriter.write("\r\n");
            //发送的内容
            m_bufferedWriter.write(m_data);
            m_bufferedWriter.flush();
            //服务器的返回
            //二进制字节流以UTF-8编码转换为字符流
            BufferedInputStream streamReader = new BufferedInputStream(m_socket.getInputStream());
            m_bufferedReader = new BufferedReader(new InputStreamReader(streamReader, "UTF-8"));
            //读取所有服务器返回的内容
            String line;
            while ((line = m_bufferedReader.readLine()) != null)
            {
                System.out.println(line);
                if ("设备添加成功".equals(line))
                {
                    result = line;
                }
            }
            m_bufferedReader.close();
            m_bufferedWriter.close();
            m_socket.close();
        }
        catch (IOException | JSONPointerException e)
        {
            e.printStackTrace();
        }
        return result;
    }

}
