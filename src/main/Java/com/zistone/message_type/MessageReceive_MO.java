package com.zistone.message_type;

import com.alibaba.fastjson.JSON;
import com.zistone.bean.DeviceInfo;
import com.zistone.bean.LocationInfo;
import com.zistone.bean.MessageType;
import com.zistone.socket.SocketHttp;
import com.zistone.util.ConvertUtil;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Date;

public class MessageReceive_MO
{
    //Web服务IP
    private static String IP_WEB;
    //Web服务端口
    private static int PORT_WEB;
    public boolean m_isRunFlag = false;
    public String[] m_detailStr;

    static
    {
        IP_WEB = PropertiesUtil.GetValueProperties().getProperty("IP_WEB");
        PORT_WEB = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_WEB"));
    }

    private Logger m_logger = Logger.getLogger(MessageReceive_MO.class);
    private DeviceInfo m_deviceInfo;

    /**
     * 终端注册
     *
     * @param tempIdStr       设备编号
     * @param bodyPropertyStr 消息体属性
     * @param typeStr         设备类型
     * @param phoneStr        手机号或设备ID
     * @param detailStr       消息流水
     * @return
     */
    private String Register(String tempIdStr, String bodyPropertyStr, String typeStr, String phoneStr, String detailStr)
    {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setM_createTime(new Date());
        deviceInfo.setM_updateTime(new Date());
        deviceInfo.setM_state(1);
        deviceInfo.setM_deviceId(tempIdStr);
        deviceInfo.setM_type(typeStr);
        deviceInfo.setM_comment("我是Socket模拟的Http请求发送过来的");
        String jsonStr = JSON.toJSONString(deviceInfo);
        //由Web服务处理终端注册
        String result = new SocketHttp().SendPost(IP_WEB, PORT_WEB, "/Blowdown_Web/DeviceInfo/InsertByDeviceId", jsonStr);
        int beginIndex = result.indexOf("{");
        int endIndex = result.lastIndexOf("}");
        result = result.substring(beginIndex, endIndex + 1);
        m_logger.debug(">>>终端注册返回:" + result);
        m_deviceInfo = JSON.parseObject(result, DeviceInfo.class);
        //终端注册应答（0x8100）
        String responseStr = "7E";
        //应答ID,对应终端消息的ID
        responseStr += "8100";
        //        responseStr += "0005";
        responseStr += bodyPropertyStr;
        responseStr += phoneStr;
        responseStr += detailStr;
        responseStr += detailStr;
        //                responseStr += idStr;
        String akCode = "";
        if (null != m_deviceInfo && m_deviceInfo.getM_id() != 0)
        {
            akCode = m_deviceInfo.getM_akCode();
            m_logger.debug(">>>服务端生成的鉴权码:" + akCode);
            //结果,0:成功1:车辆已被注册2:数据库中无该车辆3:终端已被注册4:数据库中无该终端
            if (null != akCode && !"".equals(akCode))
            {
                m_logger.debug(">>>终端注册成功");
                responseStr += "00";
                m_isRunFlag = true;
            }
            else
            {
                m_logger.debug(">>>终端注册失败");
                responseStr += "03";
            }
        }
        else
        {
            m_logger.debug(">>>终端注册失败");
            responseStr += "03";
        }
        //鉴权码
        responseStr += ConvertUtil.StrToHexStr(akCode).replaceAll("0[x|X]|,", "");
        responseStr += "A4";
        responseStr += "7E";
        return responseStr;
    }

    /**
     * 终端鉴权
     *
     * @param akCode          鉴权码
     * @param bodyPropertyStr 消息体属性
     * @param phoneStr        手机号或设备ID
     * @param detailStr       消息流水
     * @param idStr           消息ID
     * @return
     */
    private String Authoration(String akCode, String bodyPropertyStr, String phoneStr, String detailStr, String idStr)
    {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setM_akCode(akCode);
        String jsonStr = JSON.toJSONString(deviceInfo);
        //由Web服务处理终端鉴权
        String result = new SocketHttp().SendPost(IP_WEB, PORT_WEB, "/Blowdown_Web/DeviceInfo/FindByAKCode", jsonStr);
        int beginIndex = result.indexOf("{");
        int endIndex = result.lastIndexOf("}");
        result = result.substring(beginIndex, endIndex + 1);
        m_logger.debug(">>>终端鉴权返回:" + result);
        m_deviceInfo = JSON.parseObject(result, DeviceInfo.class);
        //平台通用应答(0x8001)
        String responseStr = "7E";
        //应答ID
        responseStr += "8001";
        responseStr += bodyPropertyStr;
        responseStr += phoneStr;
        responseStr += detailStr;
        responseStr += detailStr;
        responseStr += idStr;
        //结果,0:成功1:失败2:2消息有误3:不支持4:报警处理确认
        if (null != m_deviceInfo && m_deviceInfo.getM_id() != 0)
        {
            m_logger.debug(">>>终端鉴权成功");
            responseStr += "00";
        }
        else
        {
            m_logger.debug(">>>终端鉴权失败");
            responseStr += "01";
        }
        //                responseStr += checkCode;
        responseStr += "A4";
        responseStr += "7E";
        return responseStr;
    }

    /**
     * 位置信息汇报
     *
     * @param timeStr   汇报时间
     * @param phoneStr  手机号或设备ID
     * @param detailStr 消息流水
     * @param idStr     消息ID
     * @return
     */
    private String Location(String timeStr, String phoneStr, String detailStr, String idStr)
    {
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setM_deviceId(m_deviceInfo.getM_deviceId());
        locationInfo.setM_lat(m_deviceInfo.getM_lat());
        locationInfo.setM_lot(m_deviceInfo.getM_lot());
        locationInfo.setM_createTime(timeStr);
        String jsonStr = JSON.toJSONString(locationInfo);
        //由Web服务处理位置汇报
        String result = new SocketHttp().SendPost(IP_WEB, PORT_WEB, "/Blowdown_Web/LocationInfo/Insert", jsonStr);
        int beginIndex = result.indexOf("{");
        int endIndex = result.lastIndexOf("}");
        result = result.substring(beginIndex, endIndex + 1);
        m_logger.debug(">>>位置汇报返回:" + result);
        locationInfo = JSON.parseObject(result, LocationInfo.class);
        //平台通用应答(0x8001)
        String responseStr = "7E";
        //应答ID
        responseStr += "8001";
        //                    responseStr += bodyPropertyStr;
        responseStr += "0005";
        responseStr += phoneStr;
        responseStr += detailStr;
        responseStr += detailStr;
        responseStr += idStr;
        //结果,0:成功1:失败2:2消息有误3:不支持4:报警处理确认
        if (null != locationInfo && locationInfo.getM_id() != 0)
        {
            m_logger.debug(">>>位置信息汇报成功");
            responseStr += "00";
        }
        else
        {
            m_logger.debug(">>>位置信息汇报失败");
            responseStr += "01";
        }
        //                    responseStr += checkCode;
        responseStr += "A4";
        responseStr += "7E";
        return responseStr;
    }

    /**
     * 解析终端发送过来的16进制的Str
     *
     * @param hexStr
     * @return
     */
    public String RecevieHexStr(String hexStr)
    {
        String[] strArray = hexStr.split(" ");

        //返回"Error..."
        return "4572726F722E2E2E";
    }

    /**
     * 生成校验码
     * 将收到的消息还原转义后去除标识和校验位,然后按位异或得到的结果就是校验码
     *
     * @param hexStr 带空格不带0x的16进制字符串
     * @return
     */
    public String CreateCheckCode(String hexStr)
    {
        int binaryNum = 0;
        String[] strArray = hexStr.split(" ");
        for (int i = 0; i < strArray.length; i++)
        {
            int tempHexNum = Integer.parseInt(strArray[i], 16);
            if (i == 0)
            {
                binaryNum = tempHexNum;
            }
            else
            {
                binaryNum ^= tempHexNum;
            }
        }
        return Integer.toHexString(binaryNum);
    }

    public static void main(String[] args)
    {
        String str = "81 03";
        //消息体属性
        str += " 00 0A";
        //手机号或设备ID
        str += " 55 10 30 00 63 34";
        //消息流水
        String temp0 = "20";
        String temp1 = "0A";
        int temp2 = Integer.parseInt(temp1, 16) + 1;
        //不够两位前面补零
        String temp3 = Integer.toHexString(temp2);
        if (temp3.length() <= 1)
        {
            temp3 = "0" + temp3;
        }
        str += " " + temp0 + " " + temp3;
        //str += " 1F FB";
        //参数总数
        str += " 01";
        //参数列表
        str += " 00 00 00 01 04 00 00 00 14";
        //        System.out.println(new MessageReceive_MO().CreateCheckCode("81 03 00 0A 55 10 30 00 63 34 19 B5 01 00 00 00 01 04 00
        //        00 00
        //        14"));
        System.out.println(new MessageReceive_MO().CreateCheckCode(str));


        String paramStr = "09";
        //参数列表
        //终端心跳间隔(10秒)
        paramStr += " 00 01 02 00 14";
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
        //缺省时间汇报间隔
        paramStr += " 00 29 02 00 14";
        //终端工作模式(跟踪)
        paramStr += " 00 08 02 00 01";
        //跟踪模式有效时长(3600秒)
        paramStr += " 00 0B 02 0E 10";
        //跟踪模式间隔(10秒)
        paramStr += " 00 0B 02 00 0A";
        //消息体属性
        int paramSize = paramStr.split(" ").length;
        String hexParamSize = Integer.toHexString(paramSize);
        if (hexParamSize.length() <= 1)
        {
            hexParamSize = "0" + hexParamSize;
        }
        System.out.println(hexParamSize);
    }

}
