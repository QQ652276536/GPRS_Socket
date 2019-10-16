package com.zistone.socket;

import com.zistone.util.ConvertUtil;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.*;

public class Server_GPRS
{
    private static final int HEARTTIMEOUT_SOCKET;
    private static final int PORT_SOCKET;

    static
    {
        PORT_SOCKET = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET1"));
        HEARTTIMEOUT_SOCKET = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("HEARTTIMEOUT_SOCKET"));
    }

    private ServerSocket m_serverSocket;
    private Logger m_logger = Logger.getLogger(Server_GPRS.class);
    //该线程是否正在运行
    private boolean m_isRuning = false;
    private Thread m_thread;

    public Server_GPRS() throws IOException
    {
        m_serverSocket = new ServerSocket(PORT_SOCKET);
    }

    private boolean bbb = true;

    public void MyRun()
    {
        while (m_isRuning)
        {
            try
            {
                Socket socket = m_serverSocket.accept();
                if (bbb)
                {
                    bbb = false;
                    SendData(socket, "");
                }
                socket.setSoTimeout(HEARTTIMEOUT_SOCKET);
                Server_GPRS_Worker server_gprs_woker = new Server_GPRS_Worker(socket);
                Thread thread = new Thread(server_gprs_woker);
                thread.setDaemon(true);
                thread.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                m_logger.error(">>>GPRS服务开启接收数据的线程时,发生异常:" + e.getMessage());
            }
        }
        m_isRuning = false;
    }

    public void MyStart()
    {
        if (m_isRuning)
        {
            m_logger.error(">>>GPRS服务(%s)启动失败,该服务正在运行!");
        }
        else
        {
            m_isRuning = true;
            m_thread = new Thread(this::MyRun);
            m_thread.setDaemon(true);
            m_thread.start();
            m_logger.debug(String.format(">>>GPRS服务的线程%d启动...", m_thread.getId()));
        }
    }

    public void Join()
    {
        try
        {
            if (m_thread != null)
            {
                m_thread.join();
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void Stop()
    {
        m_isRuning = false;
    }

    /**
     * 发送GPRS的参数设置
     *
     * @param socket
     * @param param
     * @throws Exception
     */
    private void SendData(Socket socket, String param) throws Exception
    {
        String startUpStr = "00090000", upIntervalStr = "0000003C";
        m_logger.debug(String.format(">>>>>>>>>>>>>>>>>>>>执行发送参数设置<<<<<<<<<<<<<<<<<<<<"));
        //标志
        String hexStr = "7E";
        //消息,参数下载
        String payloadHexStr = "8103";
        //下载的参数的长度
        payloadHexStr += "0013";
        //IMEI,模拟工具为毛这里是写死的?
        payloadHexStr += "055103006334";
        //流水号
        payloadHexStr += "1998";
        //参数个数
        payloadHexStr += "02";
        //终端心跳
        //payloadHexStr += "00000001";
        //payloadHexStr += "04";
        //payloadHexStr += "00000064";
        //协议选择,0JT808协议,1简化协议
        //payloadHexStr += "0000000C";
        //payloadHexStr += "04";
        //payloadHexStr += "00000001";
        //位置汇报策略,0定时汇报,1定距汇报,2定时定距汇报
        //payloadHexStr += "00000020";
        //payloadHexStr += "04";
        //payloadHexStr += "00000000";
        //每日起始上报时间
        payloadHexStr += "00000009";
        payloadHexStr += "04";
        payloadHexStr += startUpStr;

        //上报时间间隔,单位:秒
        payloadHexStr += "00000029";
        payloadHexStr += "04";
        payloadHexStr += upIntervalStr;
        hexStr += payloadHexStr;
        //校验码
        String tempPayload = ConvertUtil.HexStrAddCharacter(payloadHexStr, " ");
        String checkCode = ConvertUtil.CreateCheckCode(tempPayload);
        hexStr += checkCode;
        //标志
        hexStr += "7E";
        m_logger.debug(">>>构建的GPRS参数设置:" + hexStr);
        byte[] byteArray = ConvertUtil.HexStrToByteArray(hexStr);
        //向铱星网关发送数据
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(byteArray);
        outputStream.flush();
        //接收铱星网关的数据
        InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[1];
        String info = "";
        StringBuffer stringBuffer = new StringBuffer();
        while (true)
        {
            if (inputStream.read(bytes) <= 0)
            {
                break;
            }
            //返回下次调用可以不受阻塞地从此流读取或跳过的估计字节数,如果等于0则表示已经读完
            String tempStr = ConvertUtil.ByteArrayToHexStr(bytes) + ",";
            stringBuffer.append(tempStr);
            //已经读完
            if (inputStream.available() == 0)
            {
                info = stringBuffer.toString();
                m_logger.debug(">>>GPRS执行参数设置后收到的信息:" + info);
                stringBuffer.delete(0, stringBuffer.length() - 1);
                break;
            }
        }
    }

}
