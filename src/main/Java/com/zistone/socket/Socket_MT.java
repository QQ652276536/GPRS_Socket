package com.zistone.socket;

import com.zistone.util.ConvertUtil;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Socket_MT
{
    private static SimpleDateFormat SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String YXGATEWAY_IP;
    private static int PORT_SOCKET;

    static
    {
        YXGATEWAY_IP = PropertiesUtil.GetValueProperties().getProperty("YXGATEWAY_IP");
        PORT_SOCKET = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET4"));
    }

    private Socket m_socket;
    private Logger m_logger = Logger.getLogger(Socket_MT.class);
    private String m_clientIdentity;

    public Socket_MT() throws IOException
    {
        m_socket = new Socket(YXGATEWAY_IP, PORT_SOCKET);
        InetSocketAddress inetSocketAddress = (InetSocketAddress) m_socket.getRemoteSocketAddress();
        String clientIP = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();
        m_clientIdentity = String.format("%s:%d", clientIP, clientPort);
    }

    /**
     * 向铱星网关发送参数设置
     * 另,铱星设备固定为监控模式
     *
     * @param data
     * @return
     */
    public String SendData(String data) throws Exception
    {
        String[] strArray = data.split("◎");
        if (strArray.length >= 3)
        {
            String imei = strArray[0];
            m_logger.debug(">>>该铱星设备的IMEI是:" + imei);
            String upTimeType = strArray[1];
            String upTimeValue = strArray[2];
            //上报间隔(分钟)
            if (upTimeType.equals("REPORTINTERVAL"))
            {
            }
            //每日起始时间
            else if (upTimeType.equals("STARTTIME"))
            {
                String[] timeArray = upTimeValue.split(",");
            }
            else
            {
                return "Error";
            }
            //版本
            String hexStr = "01";
            //总长度
            hexStr += "0035";
            //MT_HEAD_ID
            hexStr += "41";
            //MT_HEAD字段长度
            hexStr += "0015";
            //消息ID,终端自动产生,这里取系统时间数值
            Long longTime = new Date().getTime() * 1000;
            hexStr += "1C79E5B8";
            //IMEI,15位
            StringBuffer imeiBuffer = new StringBuffer();
            for (int i = 0; i < imei.length(); i++)
            {
                imeiBuffer.append("3");
                imeiBuffer.append(imei.charAt(i));
            }
            hexStr += imeiBuffer.toString();
            //优先级
            hexStr += "0000";
            //MT_PAYLOAD_ID
            hexStr += "42";
            /**
             * PAYLOAD部分
             */
            //PAYLOAD字段长度
            hexStr += "0011";
            //标志
            hexStr += "7E";
            //消息,参数下载
            String payloadHexStr = "8103";
            //平台消息流水号
            payloadHexStr += "199A";
            //参数个数
            payloadHexStr += "02";
            //上报时间间隔
            payloadHexStr += "00000029";
            //参数值的长度
            payloadHexStr += "04";
            //单位,秒
            payloadHexStr += "00000258";
            //采样时间间隔
            payloadHexStr += "00000093";
            //参数值的长度
            payloadHexStr += "04";
            //0表示在休眠时不采集位置,每次位置上报只是当前的位置
            payloadHexStr += "00000000";
            hexStr += payloadHexStr;
            //校验码
            String tempPayload = ConvertUtil.HexStrAddCharacter(payloadHexStr," ");
            String checkCode = ConvertUtil.CreateCheckCode(tempPayload);
            hexStr += checkCode;
            //标志
            hexStr += "7E";
            m_logger.debug(">>>构建的MT数据:" + hexStr);
            byte[] byteArray = ConvertUtil.HexStrToByteArray(hexStr);
            //向铱星网关发送数据
            OutputStream outputStream = m_socket.getOutputStream();
            outputStream.write(byteArray);
            //刷新缓冲
            outputStream.flush();
            //接收铱星网关的数据
            InputStream inputStream = m_socket.getInputStream();
            //一次读取一个byte
            byte[] bytes = new byte[1];
            String info = "";
            StringBuffer stringBuffer = new StringBuffer();
            while (true)
            {
                if (inputStream.read(bytes) <= 0)
                {
                    break;
                }
                inputStream.read(bytes);
                String tempStr = ConvertUtil.ByteArrayToHexStr(bytes) + ",";
                stringBuffer.append(tempStr);
                //已经读完
                if (inputStream.available() == 0)
                {
                    info = stringBuffer.toString();
                    m_logger.debug(String.format(">>>MT服务(%s)收到来自铱星网关的信息:%s", m_clientIdentity, info));
                    break;
                }
            }
            return info;
        }
        return "Null";
    }

}
