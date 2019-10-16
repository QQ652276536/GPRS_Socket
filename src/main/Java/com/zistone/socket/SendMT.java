package com.zistone.socket;

import com.zistone.util.ConvertUtil;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;

public class SendMT
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
    private Logger m_logger = Logger.getLogger(SendMT.class);
    private String m_clientIdentity;

    public SendMT() throws IOException
    {
        m_socket = new Socket(YXGATEWAY_IP, PORT_SOCKET);
        InetSocketAddress inetSocketAddress = (InetSocketAddress) m_socket.getRemoteSocketAddress();
        String clientIP = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();
        m_clientIdentity = String.format("%s:%d", clientIP, clientPort);
    }

    public SendMT(int a)
    {
    }

    public static void main(String[] args) throws Exception
    {
        new SendMT(0).SendData("300234067349750&09,00,00&3600");
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
        String[] strArray = data.split("&");
        String imeiStr;
        String startUpStr;
        String upIntervalStr;
        if (strArray.length >= 3)
        {
            imeiStr = strArray[0];
            m_logger.debug(">>>该铱星设备的IMEI是:" + imeiStr);
            String startUP = strArray[1];
            String upInterval = strArray[2];
            //每日起始时间
            String[] startUPArray = startUP.split(",");
            startUpStr = "00" + startUPArray[0] + startUPArray[1] + startUPArray[2];
            //上报间隔
            upIntervalStr = ConvertUtil.IntToHexStr(Integer.valueOf(upInterval));
            StringBuffer stringBuffer = new StringBuffer(upIntervalStr);
            int i = 8 - upIntervalStr.length();
            for (; i > 0; i--)
            {
                stringBuffer.insert(0, "0");
            }
            upIntervalStr = stringBuffer.toString();
        }
        else
        {
            return "Error";
        }
        //以下为MT数据构建
        //版本
        String hexStr = "01";
        //总长度
        hexStr += "003D";
        //MT_HEAD_ID
        hexStr += "41";
        //MT_HEAD字段长度
        hexStr += "0015";
        //消息ID,终端自动产生,这里取dayOfYear+hour+minute+second
        String dayOfYear = String.valueOf(LocalDate.now().getDayOfYear());
        String hour = String.valueOf(LocalTime.now().getHour());
        String minute = String.valueOf(LocalTime.now().getMinute());
        String second = String.valueOf(LocalTime.now().getSecond());
        String timeStr = dayOfYear + hour + minute + second;
        int timeNum = Integer.valueOf(timeStr);
        String timeHexStr = ConvertUtil.IntToHexStr(timeNum);
        //补齐4位
        if (timeHexStr.length() < 8)
        {
            StringBuffer stringBuffer = new StringBuffer(timeHexStr);
            stringBuffer.insert(0, "0");
            timeHexStr = stringBuffer.toString();
        }
        m_logger.debug(">>>生成的消息ID:" + timeHexStr);
        hexStr += timeHexStr;
        //IMEI,15位
        StringBuffer imeiBuffer = new StringBuffer();
        for (int i = 0; i < imeiStr.length(); i++)
        {
            imeiBuffer.append("3");
            imeiBuffer.append(imeiStr.charAt(i));
        }
        hexStr += imeiBuffer.toString();
        //优先级
        hexStr += "0000";
        //MT_PAYLOAD_ID
        hexStr += "42";
        //PAYLOAD字段长度,JTT808打包后的数据长度
        hexStr += "0022";
        //标志
        hexStr += "7E";
        //消息,参数下载
        String payloadHexStr = "8103";
        //下载的参数的长度
        payloadHexStr += "0013";
        //IMEI的后12位,IMEI一共15位
        //payloadHexStr += imei.substring(3);
        //模拟工具为毛这里是写死的?
        payloadHexStr += "055103006334";
        //流水号
        payloadHexStr += "1997";
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
        m_logger.debug(">>>构建的MT数据:" + hexStr);
        byte[] byteArray = ConvertUtil.HexStrToByteArray(hexStr);
        //向铱星网关发送数据
        OutputStream outputStream = m_socket.getOutputStream();
        outputStream.write(byteArray);
        outputStream.flush();
        //接收铱星网关的数据
        InputStream inputStream = m_socket.getInputStream();
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
                m_logger.debug(String.format(">>>MT服务(%s)收到来自铱星网关的信息:%s", m_clientIdentity, info));
                stringBuffer.delete(0, stringBuffer.length() - 1);
                break;
            }
        }
        return info;
    }

}
