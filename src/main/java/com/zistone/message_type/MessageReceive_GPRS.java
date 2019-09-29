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

public class MessageReceive_GPRS
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

    private Logger m_logger = Logger.getLogger(MessageReceive_GPRS.class);
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
        //前后两个标识位
        String flag1 = strArray[0];
        String flag2 = strArray[strArray.length - 1];
        //若校验码、消息头、消息体出现0x7e(~字符)则要进行转义处理,0x7e<--->0x7d0x02,0x7d<-->0x7d0x01
        //        for (int i = 0; i < strArray.length; i++)
        //        {
        //            //前后两个标识不转义
        //            if (i == 0 || i == strArray.length - 1)
        //            {
        //                continue;
        //            }
        //            else if (strArray[i].equals("7e"))
        //            {
        //                strArray[i] = "7d 02";
        //                Log.debug(">>>消息中有需要转义的字符!!!");
        //            }
        //        }
        //校验码
        String checkCode = strArray[strArray.length - 2];
        //消息头,包含消息ID、消息体属性、手机号、消息流水
        String[] tempArray = new String[strArray.length - 3];
        System.arraycopy(strArray, 1, tempArray, 0, tempArray.length);
        String[] headArray = Arrays.copyOfRange(tempArray, 0, 12);
        //消息ID
        String[] idArray = Arrays.copyOfRange(headArray, 0, 2);
        String idStr = ConvertUtil.StrArrayToStr(idArray);
        int idValue = Integer.parseInt(idStr.replaceAll("0[x|X]", ""), 16);
        //消息体属性
        String[] bodyPropertyArray = Arrays.copyOfRange(headArray, 2, 4);
        String bodyPropertyStr = ConvertUtil.StrArrayToStr(bodyPropertyArray);
        //终端手机号或终端ID
        String[] phoneArray = Arrays.copyOfRange(headArray, 4, 10);
        String phoneStr = ConvertUtil.StrArrayToStr(phoneArray);
        //消息流水
        String[] detailArray = Arrays.copyOfRange(headArray, 10, 12);
        String detailStr = ConvertUtil.StrArrayToStr(detailArray);
        m_detailStr = detailArray;
        //消息包封装项
        String[] bodyArray = Arrays.copyOfRange(tempArray, 12, tempArray.length);
        m_logger.debug(">>>校验码:" + checkCode + ",消息ID:" + idStr + ",消息体属性:" + bodyPropertyStr + ",终端手机号或终端ID:" + phoneStr + "," + "消息流水:" + detailStr);
        //根据消息ID判断消息类型
        switch (idValue)
        {
            //终端注册
            case MessageType.CLIENTREGISTER:
            {
                //省域代码
                String[] capital = Arrays.copyOfRange(bodyArray, 0, 2);
                String provinceStr = ConvertUtil.StrArrayToStr(capital);
                //市县代码
                String[] city = Arrays.copyOfRange(bodyArray, 2, 4);
                String cityStr = ConvertUtil.StrArrayToStr(city);
                //制造商
                String[] manufacture = Arrays.copyOfRange(bodyArray, 4, 9);
                String manufactureStr = ConvertUtil.StrArrayToStr(manufacture);
                //终端型号
                String[] type = Arrays.copyOfRange(bodyArray, 9, 29);
                String typeStr = "";
                //去除补位的零
                for (String tempStr : type)
                {
                    if (!"00".equals(tempStr))
                    {
                        typeStr += tempStr;
                    }
                }
                typeStr = ConvertUtil.HexStrToStr(typeStr);
                //终端ID
                String[] id = Arrays.copyOfRange(bodyArray, 29, 36);
                String tempIdStr = ConvertUtil.StrArrayToStr(id);
                tempIdStr = "Test" + idStr;
                //车牌颜色
                String[] carColor = Arrays.copyOfRange(bodyArray, 36, 37);
                String carColorStr = ConvertUtil.StrArrayToStr(carColor);
                //车辆标识(前两位为车牌归属地,后面为车牌号)
                String[] carFlag1 = Arrays.copyOfRange(bodyArray, 37, 39);
                String[] carFlag2 = Arrays.copyOfRange(bodyArray, 39, bodyArray.length);
                String carFlag2Str = ConvertUtil.StrArrayToStr(carFlag2);
                m_logger.debug(">>>收到[终端注册]的消息\r\n省域代码:" + provinceStr + ",市县代码:" + cityStr + ",制造商:" + manufactureStr + ",终端型号:" + typeStr + ",终端ID:" + tempIdStr + ",车牌颜色:" + carColorStr + ",车牌号:" + carFlag2Str);
                return Register(tempIdStr, bodyPropertyStr, typeStr, phoneStr, detailStr);
            }
            //终端鉴权
            case MessageType.CLIENTAK:
            {
                //服务端生成的鉴权码为6位,所以这里取6位的长度
                String[] akCodeArray = Arrays.copyOfRange(bodyArray, 0, 6);
                String hexAkCode = ConvertUtil.StrArrayToStr(akCodeArray);
                String akCode = ConvertUtil.HexStrToStr(hexAkCode);
                m_logger.debug(">>>收到[终端鉴权]的消息\r\n鉴权码:" + akCode + ",16进制(" + hexAkCode + ")");
                return Authoration(akCode, bodyPropertyStr, phoneStr, detailStr, idStr);
            }
            //位置信息汇报
            case MessageType.LOCATIONREPORT:
            {
                //报警标志
                String[] warningFlag = Arrays.copyOfRange(bodyArray, 0, 4);
                String warningStr = ConvertUtil.StrArrayToStr(warningFlag);
                //状态
                String[] state = Arrays.copyOfRange(bodyArray, 4, 8);
                String stateStr = ConvertUtil.StrArrayToStr(state);
                //纬度
                String[] lat = Arrays.copyOfRange(bodyArray, 8, 12);
                String latStr = ConvertUtil.StrArrayToStr(lat);
                byte[] latBytes = ConvertUtil.HexStrToByteArray(latStr);
                double latNum = (double) ConvertUtil.ByteArray4ToLong(latBytes) / 1000000;
                //经度
                String[] lot = Arrays.copyOfRange(bodyArray, 12, 16);
                String lotStr = ConvertUtil.StrArrayToStr(lot);
                byte[] lotBytes = ConvertUtil.HexStrToByteArray(lotStr);
                double lotNum = (double) ConvertUtil.ByteArray4ToLong(lotBytes) / 1000000;
                //海拔
                String[] height = Arrays.copyOfRange(bodyArray, 16, 18);
                String heightStr = ConvertUtil.StrArrayToStr(height);
                byte[] heightBytes = ConvertUtil.HexStrToByteArray(heightStr);
                double heightNum = ConvertUtil.ByteArray2ToInt(heightBytes);
                //速度
                String[] speed = Arrays.copyOfRange(bodyArray, 18, 20);
                String speedStr = ConvertUtil.StrArrayToStr(speed);
                byte[] speedBytes = ConvertUtil.HexStrToByteArray(speedStr);
                double speedNum = ConvertUtil.ByteArray2ToInt(speedBytes);
                //方向
                String[] dir = Arrays.copyOfRange(bodyArray, 20, 22);
                String dirStr = ConvertUtil.StrArrayToStr(dir);
                byte[] dirBytes = ConvertUtil.HexStrToByteArray(dirStr);
                int dirNum = ConvertUtil.ByteArray2ToInt(dirBytes);
                //时间
                String[] time = Arrays.copyOfRange(bodyArray, 22, 28);
                String year = time[0].equals("00") ? "0000" : "20" + time[0];
                String month = time[1].equals("00") ? "00" : time[1].replace("0", "");
                String day = time[2];
                String hour = time[3];
                String minute = time[4];
                String second = time[5];
                String timeStr = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
                m_logger.debug(">>>收到[位置信息汇报]的消息\r\n报警标志:" + warningStr + ",状态:" + stateStr + ",纬度:" + latNum + ",经度:" + lotNum + "," +
                        "海拨:" + heightNum + ",速度:" + speedNum + ",方向:" + dirNum + ",汇报时间:" + timeStr);
                //需要先鉴权,即判断设备是否注册成功或已经注册过
                if (null != m_deviceInfo && m_deviceInfo.getM_id() != 0)
                {
                    return Location(timeStr, phoneStr, detailStr, idStr);
                }
                else
                {
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
                    responseStr += "01";
                    //                    responseStr += checkCode;
                    responseStr += "A4";
                    responseStr += "7E";
                    m_logger.error(">>>位置信息汇报失败,需要先鉴权!\r\n");
                    return responseStr;
                }
            }
            //终端心跳,消息体为空
            case MessageType.CLIENTHEARTBEAT:
            {
                m_logger.debug(">>>收到[终端心跳]的消息");
                //收到终端的心跳消息后发送平台通用应答消息
                String responseStr = "7E8001";
                responseStr += idValue + 1;
                responseStr += "007E";
                return responseStr;
            }
            //终端通用应答
            case MessageType.CLIENTRESPONSE:
            {
                m_logger.debug(">>>收到[终端通用应答]的消息");
                break;
            }
            default:
                break;
        }
        //错误消息ID就返回"Error..."
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
        //        System.out.println(new MessageReceive_GPRS().CreateCheckCode("81 03 00 0A 55 10 30 00 63 34 19 B5 01 00 00 00 01 04 00
        //        00 00
        //        14"));
        System.out.println(new MessageReceive_GPRS().CreateCheckCode(str));


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
