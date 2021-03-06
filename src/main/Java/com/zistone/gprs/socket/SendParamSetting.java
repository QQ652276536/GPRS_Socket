package com.zistone.gprs.socket;

import com.zistone.gprs.util.MyConvertUtil;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;

public class SendParamSetting {
    private Logger _logger = Logger.getLogger(SendParamSetting.class);
    private Socket _socket;
    private String _clientIdentity;
    private String _data;

    public SendParamSetting(Socket socket, String data) {
        _socket = socket;
        _data = data;
        InetSocketAddress inetSocketAddress = (InetSocketAddress) _socket.getRemoteSocketAddress();
        String clientIP = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();
        _clientIdentity = String.format("%s:%d", clientIP, clientPort);
    }

    /**
     * 向GPRS发送参数设置
     *
     * @param detail 16进制的消息流水
     * @return
     * @throws Exception
     */
    public String SendToGPRS(String detail) throws Exception {
        if (_data == null || _data.trim().equals("")) {
            throw new Exception("GPRS设置参数不能为空");
        }
        String[] strArray = _data.split(",");
        String imeiStr;
        String setParamStr;
        if (strArray.length >= 3) {
            imeiStr = strArray[1];
            _logger.debug(String.format(">>>(%s)IMEI是:%s,消息流水是:%s", _clientIdentity, imeiStr, detail));
            setParamStr = strArray[2];
        } else {
            return "Error";
        }
        //标志
        String hexStr = "7E";
        //消息,参数下载
        String payloadHexStr = "8103";
        //下载的参数的长度
        payloadHexStr += "0013";
        //IMEI,模拟工具为毛这里是写死的?
        payloadHexStr += "055103006334";
        //流水号
        payloadHexStr += detail;
        //设置的参数
        payloadHexStr += setParamStr;
        hexStr += payloadHexStr;
        //校验码
        String tempPayload = MyConvertUtil.StrAddCharacter(payloadHexStr, 2, " ");
        String checkCode = MyConvertUtil.CreateCheckCode(tempPayload);
        hexStr += checkCode;
        //标志
        hexStr += "7E";
        _logger.debug(String.format(">>>(%s)发送GPRS设置参数:%s\r\n", _clientIdentity, hexStr));
        byte[] byteArray = MyConvertUtil.HexStrToByteArray(hexStr);
        OutputStream outputStream = _socket.getOutputStream();
        outputStream.write(byteArray);
        outputStream.flush();
        //发送参数设置后会收到终端的通用应答,这里就不再接收了
        String info = "";
        //       InputStream inputStream = _socket.getInputStream();
        //       byte[] bytes = new byte[1];
        //       StringBuffer stringBuffer = new StringBuffer();
        //       while (true)
        //       {
        //           if (inputStream.read(bytes) <= 0)
        //           {
        //               break;
        //           }
        //           //返回下次调用可以不受阻塞地从此流读取或跳过的估计字节数,如果等于0则表示已经读完
        //           String tempStr = MyConvertUtil.ByteArrayToHexStr(bytes) + ",";
        //           stringBuffer.append(tempStr);
        //           if (inputStream.available() == 0)
        //           {
        //               info = stringBuffer.toString();
        //               _logger.debug(String.format(">>>(%s)执行参数设置后,收到来自GPRS的信息:%s\r\n", _clientIdentity, info));
        //               stringBuffer.delete(0, stringBuffer.length() - 1);
        //               break;
        //           }
        //       }
        return info;
    }

    /**
     * 向铱星网关发送参数设置
     * 另,铱星设备固定为监控模式
     *
     * @return
     */
    public String SendToMT(String detail) throws Exception {
        if (_data == null || _data.trim().equals("")) {
            throw new Exception("铱星设置参数不能为空");
        }
        String[] strArray = _data.split(",");
        String imeiStr;
        String setParamStr;
        if (strArray.length >= 3) {
            imeiStr = strArray[1];
            _logger.debug(String.format(">>>(%s)IMEI是:%s", _clientIdentity, imeiStr));
            setParamStr = strArray[2];
        } else {
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
        String timeHexStr = MyConvertUtil.IntToHexStr(timeNum);
        //补齐4位
        if (timeHexStr.length() < 8) {
            int i = 8 - timeHexStr.length();
            StringBuffer stringBuffer = new StringBuffer(timeHexStr);
            for (; i > 0; i--) {
                stringBuffer.insert(0, "0");
            }
            timeHexStr = stringBuffer.toString();
        }
        _logger.debug(String.format(">>>生成的消息ID:%s", timeHexStr));
        hexStr += timeHexStr;
        //IMEI,15位
        StringBuffer imeiBuffer = new StringBuffer();
        for (int i = 0; i < imeiStr.length(); i++) {
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
        //IMEI的后12位,IMEI一共15位,模拟工具为毛这里是写死的?
        //payloadHexStr += imei.substring(3);
        payloadHexStr += "055103006334";
        //流水号
        payloadHexStr += detail;
        //设置的参数
        payloadHexStr += setParamStr;
        hexStr += payloadHexStr;
        //校验码
        String tempPayload = MyConvertUtil.StrAddCharacter(payloadHexStr, 2, " ");
        String checkCode = MyConvertUtil.CreateCheckCode(tempPayload);
        hexStr += checkCode;
        //标志
        hexStr += "7E";
        _logger.debug(String.format(">>>(%s)发送铱星设置参数:%s\r\n", _clientIdentity, hexStr));
        byte[] byteArray = MyConvertUtil.HexStrToByteArray(hexStr);
        OutputStream outputStream = _socket.getOutputStream();
        outputStream.write(byteArray);
        outputStream.flush();
        InputStream inputStream = _socket.getInputStream();
        byte[] bytes = new byte[1];
        String info = "";
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            if (inputStream.read(bytes) <= 0) {
                break;
            }
            String tempStr = MyConvertUtil.ByteArrayToHexStr(bytes) + ",";
            stringBuffer.append(tempStr);
            if (inputStream.available() == 0) {
                info = stringBuffer.toString();
                _logger.debug(String.format(">>>(%s)执行参数设置后,收到来自铱星网关的信息:%s\r\n", _clientIdentity, info));
                stringBuffer.delete(0, stringBuffer.length() - 1);
                break;
            }
        }
        inputStream.close();
        outputStream.close();
        return info;
    }

}
