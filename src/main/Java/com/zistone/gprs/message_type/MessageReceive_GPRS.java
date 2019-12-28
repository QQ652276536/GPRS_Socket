package com.zistone.gprs.message_type;

import com.alibaba.fastjson.JSON;
import com.zistone.gprs.bean.DeviceInfo;
import com.zistone.gprs.bean.MessageType;
import com.zistone.gprs.socket.SocketHttp;
import com.zistone.gprs.util.ConvertUtil;
import com.zistone.gprs.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MessageReceive_GPRS
{
    private static SimpleDateFormat SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String IP_WEB;
    private static int PORT_WEB;
    public boolean m_isRunFlag = false;

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
     * @param temperature     温度
     * @param electricity     剩余电量
     * @return
     */
    private String Register(String tempIdStr, String bodyPropertyStr, String typeStr, String phoneStr,
                            String detailStr, int temperature,
                            int electricity)
    {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setM_createTime(new Date());
        deviceInfo.setM_updateTime(new Date());
        deviceInfo.setM_state(1);
        deviceInfo.setM_deviceId(tempIdStr);
        deviceInfo.setM_type(typeStr);
        deviceInfo.setM_comment("我是Socket模拟的Http请求");
        deviceInfo.setM_temperature(temperature);
        deviceInfo.setM_electricity(electricity);
        String jsonStr = JSON.toJSONString(deviceInfo);
        //由Web服务处理终端注册
        String result = new SocketHttp().SendPost(IP_WEB, PORT_WEB, "/GPRS_Web/DeviceInfo/InsertByDeviceId", jsonStr);
        int beginIndex = result.indexOf("{");
        int endIndex = result.lastIndexOf("}");
        result = result.substring(beginIndex, endIndex + 1);
        m_logger.debug(String.format(">>>终端注册返回:%s", result));
        m_deviceInfo = JSON.parseObject(result, DeviceInfo.class);
        //终端注册应答（0x8100）
        String responseStr = "7E";
        //应答ID,对应终端消息的ID
        responseStr += "8100";
        responseStr += bodyPropertyStr;
        responseStr += phoneStr;
        responseStr += detailStr;
        responseStr += detailStr;
        String akCode = "";
        if (null != m_deviceInfo && m_deviceInfo.getM_id() != 0)
        {
            akCode = m_deviceInfo.getM_akCode();
            m_logger.debug(String.format(">>>服务端生成的鉴权码:%s", akCode));
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
    private String Authoration(String akCode, String bodyPropertyStr, String phoneStr, String detailStr,
                               String idStr) throws Exception
    {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setM_deviceId(phoneStr);
        deviceInfo.setM_akCode(akCode);
        String jsonStr = JSON.toJSONString(deviceInfo);
        //由Web服务处理终端鉴权
        String result = new SocketHttp().SendPost(IP_WEB, PORT_WEB, "/GPRS_Web/DeviceInfo/FindByAKCode", jsonStr);
        int beginIndex = result.indexOf("{");
        int endIndex = result.lastIndexOf("}");
        result = result.substring(beginIndex, endIndex + 1);
        m_logger.debug(String.format(">>>终端鉴权返回:%s", result));
        m_deviceInfo = JSON.parseObject(result, DeviceInfo.class);
        String hexStr = "7E";
        //平台通用应答(0x8001)
        String payloadHexStr = "8001";
        int bodyProperty = Integer.parseInt(bodyPropertyStr, 16);
        bodyPropertyStr = ConvertUtil.IntToHexStr(++bodyProperty);
        //补齐4位
        if (bodyPropertyStr.length() < 4)
        {
            int i = 4 - bodyPropertyStr.length();
            StringBuffer stringBuffer = new StringBuffer(bodyPropertyStr);
            for (; i > 0; i--)
            {
                stringBuffer.insert(0, "0");
            }
            bodyPropertyStr = stringBuffer.toString();
        }
        payloadHexStr += bodyPropertyStr;
        payloadHexStr += phoneStr;
        payloadHexStr += detailStr;
        payloadHexStr += detailStr;
        payloadHexStr += idStr;
        //payloadHexStr += "0005055103006334199723040102";
        //结果,0:成功1:失败2:2消息有误3:不支持4:报警处理确认
        if (null != m_deviceInfo && m_deviceInfo.getM_id() != 0)
        {
            m_logger.debug(String.format(">>>存在该鉴权码:%s", akCode));
            payloadHexStr += "00";
        }
        else
        {
            m_logger.error(String.format(">>>不存在该鉴权码:%s", akCode));
            payloadHexStr += "01";
        }
        hexStr += payloadHexStr;
        //校验码
        String tempPayload = ConvertUtil.HexStrAddCharacter(payloadHexStr, " ");
        String checkCode = ConvertUtil.CreateCheckCode(tempPayload);
        hexStr += checkCode;
        hexStr += "7E";
        return hexStr;
    }

    /**
     * 位置信息汇报
     *
     * @param phoneStr    手机号或设备ID
     * @param detailStr   消息流水
     * @param idStr       消息ID
     * @param lat         纬度
     * @param lot         经度
     * @param height      高度
     * @param dateTime    汇报时间
     * @param temperature 温度
     * @param electricity 剩余电量
     * @return
     */
    private String Location(String phoneStr, String detailStr, String idStr, double lat, double lot, int height,
                            Date dateTime,
                            int temperature, int electricity)
    {
        if (lat == 0 || lot == 0)
        {
            m_logger.error(String.format(">>>数据解析有误,停止注册/更新/汇报GPRS设备%s的位置!", phoneStr));
            return "Error...";
        }
        //平台通用应答(0x8001)
        String responseStr = "7E";
        responseStr += "8001";
        responseStr += "0005";
        responseStr += phoneStr;
        responseStr += detailStr;
        responseStr += detailStr;
        responseStr += idStr;
        if (m_deviceInfo != null && m_deviceInfo.getM_id() != 0)
        {
            m_deviceInfo.setM_lat(lat);
            m_deviceInfo.setM_lot(lot);
            m_deviceInfo.setM_height(height);
            m_deviceInfo.setM_temperature(temperature);
            m_deviceInfo.setM_electricity(electricity);
            String jsonStr = JSON.toJSONString(m_deviceInfo);
            //由Web服务处理位置汇报
            String result = new SocketHttp().SendPost(IP_WEB, PORT_WEB, "/GPRS_Web/DeviceInfo/Update", jsonStr);
            int beginIndex = result.indexOf("{");
            int endIndex = result.lastIndexOf("}");
            result = result.substring(beginIndex, endIndex + 1);
            m_logger.debug(String.format(">>>GPRS设备%s汇报位置后返回:%s", m_deviceInfo.getM_deviceId(), result));
            m_deviceInfo = JSON.parseObject(result, DeviceInfo.class);
            //判断设备信息是否更新成功
            //结果,0:成功1:失败2:2消息有误3:不支持4:报警处理确认
            if (null != m_deviceInfo && m_deviceInfo.getM_id() != 0)
            {
                m_logger.debug(String.format(">>>设备%s的位置信息汇报成功", m_deviceInfo.getM_deviceId()));
                responseStr += "00";
            }
            else
            {
                m_logger.debug(String.format(">>>设备%s的位置信息汇报失败", phoneStr));
                responseStr += "01";
            }
        }
        else
        {
            responseStr += "01";
            m_logger.error(String.format(">>>设备%s的位置信息汇报失败,需要先鉴权!\r\n", phoneStr));
        }
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
        try
        {
            String[] strArray = hexStr.split(",");
            //过滤掉开头和结尾的逗号分割符
            List<String> list = new ArrayList<>();
            for (int i = 0; i < strArray.length; i++)
            {
                if (i == 0 && strArray[i].equals(""))
                {
                    continue;
                }
                if (i == strArray.length - 1 && strArray[i].equals(""))
                {
                    continue;
                }
                list.add(strArray[i]);
            }
            list.toArray(strArray);
            //前后两个标识位
            String flag1 = strArray[0];
            String flag2 = strArray[strArray.length - 1];
            //校验码
            String checkCode = strArray[strArray.length - 2];
            //消息ID
            String idStr = strArray[1] + strArray[2];
            int idValue = Integer.parseInt(idStr, 16);
            //消息体属性
            String bodyPropertyStr = strArray[3] + strArray[4];
            //终端手机号或终端ID
            String phoneStr = strArray[5] + strArray[6] + strArray[7] + strArray[8] + strArray[9] + strArray[10];
            //消息流水
            String detailStr = strArray[11] + strArray[12];
            //消息包封装项
            String[] bodyArray = Arrays.copyOfRange(strArray, 13, strArray.length - 2);
            m_logger.debug(String.format(">>>消息ID:%s,消息体属性:%s,终端手机号或终端ID:%s,消息流水:%s,校验码:%s", idStr, bodyPropertyStr,
                    phoneStr, detailStr, checkCode));
            //根据消息ID判断消息类型
            switch (idValue)
            {
                //终端注册
                case MessageType.CLIENTREGISTER:
                {
                    //省域代码
                    String provinceStr = bodyArray[0] + bodyArray[1];
                    //市县代码
                    String cityStr = bodyArray[2] + bodyArray[3];
                    //制造商
                    String manufactureStr = bodyArray[4] + bodyArray[5] + bodyArray[6] + bodyArray[7] + bodyArray[8];
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
                    String tempIdStr =
                            bodyArray[29] + bodyArray[30] + bodyArray[31] + bodyArray[32] + bodyArray[33] + bodyArray[34] + bodyArray[35];
                    //车牌颜色
                    String carColorStr = bodyArray[36] + bodyArray[37];
                    //车辆标识(前两位为车牌归属地,后面为车牌号)
                    String carFlag1Str = bodyArray[38] + bodyArray[39];
                    String[] carFlag2 = Arrays.copyOfRange(bodyArray, 39, bodyArray.length);
                    String carFlag2Str = ConvertUtil.StrArrayToStr(carFlag2);
                    m_logger.debug(String.format(">>>该消息为[终端注册],省域代码:%s,市县代码:%s," +
                                    "制造商:%s,终端型号:%s,终端ID:%s,车牌颜色:%s,车牌号:%s", provinceStr, cityStr, manufactureStr,
                            typeStr,
                            tempIdStr, carColorStr, carFlag2Str));
                    return Register(tempIdStr, bodyPropertyStr, typeStr, phoneStr, detailStr, 0, 0);
                }
                //终端鉴权
                case MessageType.CLIENTAK:
                {
                    //服务端生成的鉴权码为6位,所以这里取6位的长度
                    String hexAkCode =
                            bodyArray[0] + bodyArray[1] + bodyArray[2] + bodyArray[3] + bodyArray[4] + bodyArray[5];
                    String akCode = ConvertUtil.HexStrToStr(hexAkCode);
                    m_logger.debug(String.format(">>>该消息为[终端鉴权],鉴权码:%s(16进制:%s)", akCode, hexAkCode));
                    return Authoration(akCode, bodyPropertyStr, phoneStr, detailStr, idStr) + ",SETPARAM";
                }
                //位置信息汇报
                case MessageType.LOCATIONREPORT:
                {
                    //报警标志
                    String warningStr = bodyArray[0] + bodyArray[1] + bodyArray[2] + bodyArray[3];
                    //状态
                    String stateStr = bodyArray[4] + bodyArray[5] + bodyArray[6] + bodyArray[7];
                    //纬度
                    String latStr = bodyArray[8] + bodyArray[9] + bodyArray[10] + bodyArray[11];
                    double latNum = Double.valueOf(Integer.valueOf(latStr, 16)) / 1000000;
                    //经度
                    String lotStr = bodyArray[12] + bodyArray[13] + bodyArray[14] + bodyArray[15];
                    double lotNum = Double.valueOf(Integer.valueOf(lotStr, 16)) / 1000000;
                    //海拔
                    String heightStr = bodyArray[16] + bodyArray[17];
                    byte[] heightBytes = ConvertUtil.HexStrToByteArray(heightStr);
                    int heightNum = ConvertUtil.ByteArray2ToInt(heightBytes);
                    //速度
                    String speedStr = bodyArray[18] + bodyArray[19];
                    byte[] speedBytes = ConvertUtil.HexStrToByteArray(speedStr);
                    double speedNum = ConvertUtil.ByteArray2ToInt(speedBytes);
                    //方向
                    String dirStr = bodyArray[20] + bodyArray[21];
                    byte[] dirBytes = ConvertUtil.HexStrToByteArray(dirStr);
                    int dirNum = ConvertUtil.ByteArray2ToInt(dirBytes);
                    //时间
                    String year = bodyArray[22].equals("00") ? "0000" : "20" + bodyArray[22];
                    String month = bodyArray[23];
                    String day = bodyArray[24];
                    String hour = bodyArray[25];
                    String minute = bodyArray[26];
                    String second = bodyArray[27];
                    String timeStr = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
                    Date dateTime = SIMPLEDATEFORMAT.parse(timeStr);
                    //温度
                    int temperatureNum = 0;
                    //电量
                    int electricityNum = 0;
                    m_logger.debug(String.format(">>>该消息为[位置信息汇报],报警标志:%s,状态:%s,纬度:%s,经度:%s,海拨:%d,温度:%d,电量:%d,速度:%s," +
                                    "方向:%d,汇报时间:%s", warningStr, stateStr, latNum, lotNum, heightNum, temperatureNum,
                            electricityNum,
                            speedNum, dirNum, timeStr));
                    return Location(phoneStr, detailStr, idStr, latNum, lotNum, heightNum, dateTime, temperatureNum,
                            electricityNum);
                }
                //定位数据批量上传
                case MessageType.LOCATIONBATCHUP:
                {
                    m_logger.debug(">>>该消息为[定位数据批量上传],暂未支持该消息的解析...");
                    break;
                }
                //终端心跳,消息体为空
                case MessageType.CLIENTHEARTBEAT:
                {
                    m_logger.debug(">>>该消息为[终端心跳]");
                    //收到终端的心跳消息后发送平台通用应答消息
                    String responseStr = "7E8001";
                    responseStr += ++idValue;
                    responseStr += "007E";
                    return responseStr;
                }
                //终端通用应答
                case MessageType.CLIENTRESPONSE:
                {
                    m_logger.debug(">>>该消息为[终端通用应答]");
                    break;
                }
                default:
                    m_logger.debug(">>>该消息无法解析");
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            m_logger.error(String.format(">>>解析内容时发生异常:", e.getMessage()));
        }
        return "";
    }

}
