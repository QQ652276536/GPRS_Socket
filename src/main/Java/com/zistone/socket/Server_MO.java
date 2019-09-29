package com.zistone.socket;

import com.zistone.message_type.MessageReceive_MO;
import com.zistone.util.ConvertUtil;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Server_MO extends Thread
{
    //心跳超时时间
    private static int TIMEOUT;

    static
    {
        TIMEOUT = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("HEARTTIMEOUT_SOCKET"));
    }

    private final ServerSocket m_serverSocket;
    private Socket m_socket;
    private Logger m_logger = Logger.getLogger(Server_MO.class);
    //接收到数据的最新时间
    private long m_lastReceiveTime = System.currentTimeMillis();
    //该线程是否正在运行
    private boolean m_isRuning = false;

    public Server_MO(int port) throws IOException
    {
        m_serverSocket = new ServerSocket(port);
    }

    @Override
    public void start()
    {
        if (m_isRuning)
        {
            m_logger.error(">>>MO的线程" + this.getId() + "启动失败,该线程正在执行!");
        }
        else
        {
            m_isRuning = true;
            super.start();
            m_logger.debug(">>>MO的线程" + this.getId() + "启动,端口:" + m_serverSocket.getLocalPort() + ",等待终端连接...");
        }
    }

    @Override
    public void run()
    {
        while (m_isRuning)
        {
            //开始监听
            try
            {
                m_socket = m_serverSocket.accept();
                //读写超时时间（5秒）
                m_socket.setSoTimeout(5000);
                //字节输入流
                InputStream inputStream;
                //字节输出流
                OutputStream outputStream;
                String info = "";
                //按byte读
                byte[] bytes = new byte[1];
                MessageReceive_MO messageReceive_mo = new MessageReceive_MO();
                inputStream = m_socket.getInputStream();
                outputStream = m_socket.getOutputStream();
                while (m_isRuning)
                {
                    //检测心跳
                    if (System.currentTimeMillis() - m_lastReceiveTime > TIMEOUT)
                    {
                        m_isRuning = false;
                        m_logger.debug(">>>线程" + this.getId() + "的连接已超时");
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
                            m_logger.debug(">>>MO的线程" + this.getId() + "接收到:" + info);
                            //模拟业务处理Thread.sleep(1000);
                            String responseStr = "";
                            if (!"".equals(info))
                            {
                                //解析收到的内容并响应
                                responseStr = messageReceive_mo.RecevieHexStr(info);
                            }
                            byte[] byteArray = ConvertUtil.HexStrToByteArray(responseStr);
                            outputStream.write(byteArray);
                            outputStream.flush();
                            //重置接收的数据
                            info = "";
                            m_logger.debug(">>>MO的线程" + this.getId() + "生成的响应内容:" + responseStr);
                        }
                    }
                }
            }
            catch (SocketTimeoutException | SocketException e)
            {
                m_logger.error(">>>MO的线程" + this.getId() + "读取超时!");
                e.printStackTrace();
            }
            catch (IOException e)
            {
                m_logger.error(">>>MO的线程" + this.getId() + "读取异常!");
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    m_socket.close();
                }
                catch (IOException e)
                {
                    m_logger.error(String.format(">>>MO的线程%d关闭Socket时出现错误:%s", this.getId(), e.getMessage()));
                    e.printStackTrace();
                }
            }
        }
    }

}
