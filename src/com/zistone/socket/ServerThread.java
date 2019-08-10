package com.zistone.socket;

import com.zistone.message_type.MessageReceive;
import com.zistone.util.ConvertUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

/**
 * 长连接
 */
public class ServerThread extends Thread
{
    private static Logger LOG = Logger.getLogger(ServerThread.class);
    //心跳超时时间
    private static final int TIMEOUT = 60 * 1000;
    private Socket m_socket;
    //接收到数据的最新时间
    private long m_lastReceiveTime = System.currentTimeMillis();
    //该线程是否正在运行
    private boolean m_isRuning = false;

    public ServerThread(Socket socket)
    {
        this.m_socket = socket;
    }

    @Override
    public void start()
    {
        if (m_isRuning)
        {
            LOG.debug(">>>线程" + this.getId() + "启动失败,该线程正在执行");
            return;
        }
        else
        {
            m_isRuning = true;
            super.start();
        }
    }

    @Override
    public void run()
    {
        //字节输入流
        InputStream inputStream = null;
        //字节输出流
        OutputStream outputStream = null;
        try
        {
            inputStream = m_socket.getInputStream();
            outputStream = m_socket.getOutputStream();
            String info = "";
            //按byte读
            byte[] bytes = new byte[1];
            MessageReceive messageReceive = new MessageReceive();
            while (m_isRuning)
            {
                //检测心跳
                if (System.currentTimeMillis() - m_lastReceiveTime > TIMEOUT)
                {
                    m_isRuning = false;
                    //跳出,执行finally块
                    break;
                }
                //返回下次调用可以不受阻塞地从此流读取或跳过的估计字节数,如果等于0则表示已经读完
                if (inputStream.available() > 0)
                {
                    //重置接收到数据的最新时间
                    m_lastReceiveTime = System.currentTimeMillis();
                    inputStream.read(bytes);
                    String tempStr = ConvertUtil.ByteArrayToHexStr(bytes) + " ";
                    info += tempStr;
                    //已经读完
                    if (inputStream.available() == 0)
                    {
                        LOG.debug(">>>线程" + this.getId() + "接收到:" + info);
                        //模拟业务处理Thread.sleep(10000);
                        String responseStr = "";
                        if (!"".equals(info))
                        {
                            //解析收到的内容
                            responseStr = messageReceive.RecevieHexStr(info);
                        }
                        byte[] byteArray = ConvertUtil.HexStrToByteArray(responseStr);
                        outputStream.write(byteArray);
                        outputStream.flush();
                        //重置接收的数据
                        info = "";
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //关闭资源
        finally
        {
            LOG.debug(">>>线程" + this.getId() + "的连接已断开\n");
            try
            {
                if (outputStream != null)
                    outputStream.close();
                if (inputStream != null)
                    inputStream.close();
                if (m_socket != null)
                    m_socket.close();
                m_isRuning = false;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

}
