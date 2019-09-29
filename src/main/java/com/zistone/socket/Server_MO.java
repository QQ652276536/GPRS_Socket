package com.zistone.socket;

import com.zistone.message_type.MessageReceive_MO;
import com.zistone.util.ConvertUtil;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

class Server_MO_worker implements Runnable
{
    private Logger logger;
    private Socket m_socket;
    private boolean m_isRuning;
    private String m_clientIdentity;

    Server_MO_worker(Logger logger, Socket socket) throws Exception
    {
        this.logger = logger;
        this.m_socket = socket;
        m_isRuning = false;

        InetSocketAddress address = (InetSocketAddress) m_socket.getRemoteSocketAddress();
        String clientIP = address.getAddress().getHostAddress();
        int clientPort = address.getPort();
        m_clientIdentity = String.format("%s:%d", clientIP, clientPort);
    }

    private void run0() throws Exception
    {
        MessageReceive_MO messageReceive_mo_gprs = new MessageReceive_MO();

        //字节输入流
        InputStream inputStream = m_socket.getInputStream();
        //字节输出流
        OutputStream outputStream = m_socket.getOutputStream();;

        StringBuilder buffer = new StringBuilder();

        //按byte读
        byte[] bytes = new byte[1];
        m_isRuning = true;
        while (m_isRuning)
        {
            int n = inputStream.read(bytes);
            if(n <= 0) {
                break;
            }

            String tempStr = ConvertUtil.ByteArrayToHexStr(bytes) + " ";
            buffer.append(tempStr);

            //已经读完
            if (inputStream.available() == 0)
            {
                String responseStr;
                String info = buffer.toString();
                buffer.delete(0, buffer.length() - 1); // 清空缓冲区
                logger.debug(String.format(">>>MO服务(%s), 收到:%s", m_clientIdentity, info));

                if (!info.isEmpty())  {
                    //解析收到的内容并响应
                    responseStr = messageReceive_mo_gprs.RecevieHexStr(info);
                } else {
                    responseStr = "";
                }

                logger.debug(String.format(">>>MO服务(%s), 生成的响应内容: %s", m_clientIdentity, responseStr));

                byte[] byteArray = ConvertUtil.HexStrToByteArray(responseStr);
                outputStream.write(byteArray);
                outputStream.flush();
            }
        }
    }

    @Override
    public void run()
    {
        try {
            run0();
        } catch (SocketTimeoutException e) {
            logger.debug(String.format(">>>MO(%s), 读取超时", m_clientIdentity));
        } catch (Exception e) {
            logger.debug(String.format(">>>MO(%s), 读取异常: %s", m_clientIdentity, e.getMessage()));
        }

        m_isRuning = false;

        try {
            m_socket.close();
        } catch (Exception e) {
            logger.debug(String.format(">>>MO(%s), 关闭 socket时出现错误:%s", m_clientIdentity, e.getMessage()));
        }
    }
}

/**
 * 长连接
 */
public class Server_MO
{
    private static final Logger logger = Logger.getLogger(Server_MO.class);

    private static final int TIMEOUT;
    static  {
        TIMEOUT = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("HEARTTIMEOUT_SOCKET"));
    }

    private final ServerSocket m_serverSocket;
    private boolean m_isRuning = false;
    private Thread m_thread;

    public Server_MO(int port) throws Exception
    {
        m_serverSocket = new ServerSocket(port);
    }

    public boolean start()
    {
        if(m_isRuning) {
            logger.error(">>>MO服务启动失败，该服务正在执行!");
        } else {
            m_isRuning = true;
            m_thread = new Thread(this::run);
            m_thread.setDaemon(true);
            m_thread.setName("Server_MO thread");
            m_thread.start();
            logger.debug(">>>MO服务启动成功...");
            return true;
        }
        return false;
    }

    public void join() throws Exception
    {
        if(m_thread != null) {
            m_thread.join();
        }
    }

    public void stop() throws Exception
    {
        m_isRuning = false;
        if(m_thread != null) {
            m_thread.join();
        }
    }

    private void run()
    {
        while (m_isRuning)
        {
            try {
                Socket socket = m_serverSocket.accept();
                socket.setSoTimeout(TIMEOUT); // Socket 的读写超时设置.

                Server_MO_worker worker = new Server_MO_worker(logger, socket);
                Thread thread = new Thread(worker);
                thread.setDaemon(true);
                thread.start();

            } catch (Exception e) {
                e.printStackTrace();
                logger.debug(">>>MO服务, 未知错误: " + e.getMessage());
                break;
            }
        }
        m_isRuning = false;
    }

    private void TestSendComm(MessageReceive_MO messageReceive_mo_gprs, OutputStream outputStream) throws IOException
    {
        if (messageReceive_mo_gprs.m_isRunFlag)
        {
            messageReceive_mo_gprs.m_isRunFlag = false;
            //参数总数
            String paramStr = "07";
            //参数列表
            //终端心跳间隔(10秒)
            paramStr += " 00 01 02 00 0A";
            //TCP消息应答超时时间(30秒)
            paramStr += " 00 02 02 00 1E";
            //TCP消息重传次数(3次)
            paramStr += " 00 03 02 00 03";
            //UDP消息应答超时时间(30秒)
            paramStr += " 00 04 02 00 1E";
            //UDP消息重传次数(3次)
            paramStr += " 00 05 02 00 03";
            //位置汇报策略(0定时1定距2定时定距)
            paramStr += " 00 20 02 00 00";
            //休眠时汇报时间间隔(10秒)
            //paramStr += " 00 27 02 00 0A";
            //缺省时间汇报间隔(10秒)
            paramStr += " 00 29 02 00 1E";
            //终端工作模式(跟踪)
            //paramStr += " 00 00 00 08 04 00 00 00 01";
            //跟踪模式有效时长(3600秒)
            //paramStr += " 00 00 00 0B 04 00 00 0E 10";
            //跟踪模式间隔(10秒)
            //paramStr += " 00 00 00 0B 04 00 00 00 0A";
            String str = "81 03";
            //消息体属性
            int paramSize = paramStr.split(" ").length;
            String hexParamSize = Integer.toHexString(paramSize);
            if (hexParamSize.length() <= 1)
            {
                hexParamSize = "0" + hexParamSize;
            }
            str += " 00";
            str += " " + hexParamSize;
            //手机号或设备ID
            str += " 55 10 30 00 63 34";
            //消息流水
            String temp0 = messageReceive_mo_gprs.m_detailStr[0];
            String temp1 = messageReceive_mo_gprs.m_detailStr[1];
            int temp2 = Integer.parseInt(temp1, 16) + 1;
            //不够两位前面补零
            String temp3 = Integer.toHexString(temp2);
            if (temp3.length() <= 1)
            {
                temp3 = "0" + temp3;
            }
            str += " " + temp0 + " " + temp3;
            logger.debug("生成新的流水号:" + temp0 + temp3);
            //str += " 1F FB";
            //校验码
            String checkCode = messageReceive_mo_gprs.CreateCheckCode(paramStr);
            if (checkCode.length() <= 1)
            {
                checkCode = "0" + checkCode;
            }
            String reponseStr = ("7E" + str + paramStr + checkCode + "7E").replaceAll(" ", "");
            byte[] tempByteArray = ConvertUtil.HexStrToByteArray(reponseStr);
            //tempByteArray = ConvertUtil.HexStrToByteArray("7E8103000A55103000633419B501000000010400000014167E");
            outputStream.write(tempByteArray);
            outputStream.flush();
            logger.debug("\r\n>>>执行下发命令:" + reponseStr + "\r\n");
        }
    }

}
