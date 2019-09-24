package com.zistone.socket;

import com.zistone.message_type.MessageReceive;
import com.zistone.util.ConvertUtil;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

/**
 * 长连接
 */
public class ServerThread extends Thread
{
    //心跳超时时间
    private static int TIMEOUT;

    static
    {
        TIMEOUT = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("HEARTTIMEOUT_SOCKET"));
    }

    private Socket m_socket;
    private Logger m_logger = Logger.getLogger(ServerThread.class);
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
            m_logger.debug(">>>线程" + this.getId() + "启动失败,该线程正在执行");
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
                    m_logger.debug(">>>线程" + this.getId() + "的连接已超时");
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
                        m_logger.debug(">>>线程" + this.getId() + "接收到:" + info);
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
                        if (messageReceive.m_isRunFlag)
                        {
                            messageReceive.m_isRunFlag = false;
                            String str = "81 03";
                            //消息体属性
                            str += " 00 0A";
                            //手机号或设备ID
                            str += " 55 10 30 00 63 34";
                            //消息流水
                            String temp0 = messageReceive.m_detailStr[0];
                            String temp1 = messageReceive.m_detailStr[1];
                            int temp2 = Integer.parseInt(temp1, 16) + 1;
                            //不够两位前面补零
                            String temp3 = Integer.toHexString(temp2);
                            if (temp3.length() <= 1)
                            {
                                temp3 = "0" + temp3;
                            }
                            str += " " + temp0 + " " + temp3;
                            m_logger.debug("生成新的流水号:" + temp0 + temp3);
                            //str += " 1F FB";
                            //参数总数
                            str += " 01";
                            //参数列表
                            str += " 00 00 00 01 04 00 00 00 14";
                            //校验码
                            String checkCode = messageReceive.CreateCheckCode(str);
                            String reponseStr = ("7E" + str + checkCode + "7E").replaceAll(" ", "");
                            byte[] tempByteArray = ConvertUtil.HexStrToByteArray(reponseStr);
                            tempByteArray = ConvertUtil.HexStrToByteArray("7E8103000A55103000633419B501000000010400000014167E");
                            outputStream.write(tempByteArray);
                            outputStream.flush();
                            m_logger.debug("\r\n>>>位置信息汇报成功,执行下发命令:" + reponseStr + "\r\n");
                        }
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
            m_logger.debug(">>>线程" + this.getId() + "的连接已断开\r\n");
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
