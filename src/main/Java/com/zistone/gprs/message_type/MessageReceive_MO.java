package com.zistone.gprs.message_type;

import com.alibaba.fastjson.JSON;
import com.zistone.gprs.bean.DeviceInfo;
import com.zistone.gprs.bean.LocationInfo;
import com.zistone.gprs.socket.SocketHttp;
import com.zistone.gprs.util.MyConvertUtil;
import com.zistone.gprs.util.MyPropertiesUtil;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MessageReceive_MO
{
    private static SimpleDateFormat SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String _webIP;
    private static int _webPort;

    static
    {
        _webIP = MyPropertiesUtil.GetValueProperties().getProperty("IP_WEB");
        _webPort = Integer.valueOf(MyPropertiesUtil.GetValueProperties().getProperty("PORT_WEB"));
    }

    private Logger _logger = Logger.getLogger(MessageReceive_MO.class);

    /**
     * 位置信息汇报
     *
     * @param lat         纬度
     * @param lot         经度
     * @param height      高度
     * @param tempIdStr   设备编号
     * @param typeStr     设备类型
     * @param dateTime    汇报时间
     * @param temperature 温度
     * @param electricity 剩余电量
     * @return
     */
    private String Location(double lat, double lot, int height, String tempIdStr, String typeStr, Date dateTime, int temperature,
                            int electricity)
    {
        if (lat == 0 || lot == 0)
        {
            _logger.error(String.format(">>>数据解析有误,停止注册/更新/汇报铱星设备%s的位置!", tempIdStr));
            return "Error...";
        }
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId(tempIdStr);
        deviceInfo.setLat(lat);
        deviceInfo.setLot(lot);
        deviceInfo.setHeight(height);
        deviceInfo.setCreateTime(dateTime);
        deviceInfo.setUpdateTime(dateTime);
        deviceInfo.setState(1);
        deviceInfo.setType(typeStr);
        deviceInfo.setComment("我是Socket模拟的Http请求");
        deviceInfo.setTemperature(temperature);
        deviceInfo.setElectricity(electricity);
        String deviceJsonStr = JSON.toJSONString(deviceInfo);
        //由Web服务处理终端注册
        String deviceResult = new SocketHttp().SendPost(_webIP, _webPort, "/GPRS_Web/DeviceInfo/InsertByDeviceId", deviceJsonStr);
        int beginIndex = deviceResult.indexOf("{");
        int endIndex = deviceResult.lastIndexOf("}");
        deviceResult = deviceResult.substring(beginIndex, endIndex + 1);
        _logger.debug(String.format(">>>注册/更新铱星设备%s,返回:%s", tempIdStr, deviceResult));
        deviceInfo = JSON.parseObject(deviceResult, DeviceInfo.class);
        //同时在历史轨迹里新增一条记录
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setDeviceId(tempIdStr);
        locationInfo.setLat(lat);
        locationInfo.setLot(lot);
        locationInfo.setCreateTime(dateTime);
        String locationStr = JSON.toJSONString(locationInfo);
        //由Web服务处理位置汇报
        String locationResult = new SocketHttp().SendPost(_webIP, _webPort, "/GPRS_Web/LocationInfo/Insert", locationStr);
        int beginIndex2 = locationResult.indexOf("{");
        int endIndex2 = locationResult.lastIndexOf("}");
        locationResult = locationResult.substring(beginIndex2, endIndex2 + 1);
        _logger.debug(String.format(">>>汇报铱星设备轨迹位置,返回:%s", locationResult));
        locationInfo = JSON.parseObject(locationResult, LocationInfo.class);
        return deviceResult + "&" + locationResult;
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
            /**
             * 全部数据由MO_HEAD + MO_LOCATION + PAYLOAD几部分组成
             */
            //版本,例如:01
            String version = strArray[0];
            if (!version.equals("01"))
            {
                int length = Integer.valueOf(strArray[4]);
                if (length > 0)
                {
                }
                String str = MyConvertUtil.HexStrToStr(Arrays.toString(strArray));
                _logger.debug(String.format("\n\n\n>>>消息版本不为01(未知协议),尝试解析该内容:%s\n\n\n", str));

                //应用服务器发送MT给铱星网关确认收到MO的数据
                //01,版本
                //0004,总长度
                //05,MT_CONFIRMATION_ID
                //0001,长度
                //01,接收成功标志
                return "01000405000101";
            }
            //总长度,例如:007E
            String totalLengthStr = strArray[1] + strArray[2];
            int totalLength = Integer.parseInt(totalLengthStr, 16);
            /**
             * MO_HEAD
             */
            //MO_HEAD标志,例如:01
            String mo_head_flag = strArray[3];
            //MO_HEAD长度,例如:001C
            String mo_head_length = strArray[4] + strArray[5];
            //消息ID,终端自动产生,例如:4C4AF615
            String messageId = strArray[6] + strArray[7] + strArray[8] + strArray[9];
            //IMEI,例如:333030323334303630323135393730,表示300234060215970
            String imeiStr = strArray[10].substring(1);
            imeiStr += strArray[11].substring(1);
            imeiStr += strArray[12].substring(1);
            imeiStr += strArray[13].substring(1);
            imeiStr += strArray[14].substring(1);
            imeiStr += strArray[15].substring(1);
            imeiStr += strArray[16].substring(1);
            imeiStr += strArray[17].substring(1);
            imeiStr += strArray[18].substring(1);
            imeiStr += strArray[19].substring(1);
            imeiStr += strArray[20].substring(1);
            imeiStr += strArray[21].substring(1);
            imeiStr += strArray[22].substring(1);
            imeiStr += strArray[23].substring(1);
            imeiStr += strArray[24].substring(1);
            //会话状态,例如:成功00
            String sessionState = strArray[25];
            //MO_MSN,例如:01EA
            String mo_msn = strArray[26] + strArray[27];
            //MT_MSN,例如:0000
            String mt_msn = strArray[28] + strArray[29];
            //EPOCH时间,例如:5C2DED4D
            String epoch_time = strArray[30] + strArray[31] + strArray[32] + strArray[33];
            Long longTime = Long.parseLong(epoch_time, 16) * 1000;
            String timeStr = SIMPLEDATEFORMAT.format(new Date(longTime));
            /**
             * MO_LOCATION
             *
             * 铱星的定位信息
             */
            //位置信息头,例如:03
            String head = strArray[34];
            //长度,例如:000B
            String lengthStr = strArray[35] + strArray[36];
            int length = Integer.parseInt(lengthStr, 16);
            //定位状态,例如:00
            String state = strArray[37];
            //纬度,例如:167D12
            String lat1Str = String.valueOf(Integer.parseInt(strArray[38], 16));
            String lat2Str = String.valueOf(Integer.parseInt(strArray[39], 16));
            String lat3Str = String.valueOf(Integer.parseInt(strArray[40], 16));
            double latNumXxx = Double.valueOf(lat1Str + lat2Str + lat3Str) / 1000000;
            //经度,例如:71DFBB
            String lot1Str = String.valueOf(Integer.parseInt(strArray[41], 16));
            String lot2Str = String.valueOf(Integer.parseInt(strArray[42], 16));
            String lot3Str = String.valueOf(Integer.parseInt(strArray[43], 16));
            double lotNumXxx = Double.valueOf(lot1Str + lot2Str + lot3Str) / 1000000;
            //CEP半径,例如:00000009
            String cepStr = strArray[44] + strArray[45] + strArray[46] + strArray[47];
            if (strArray.length < 64)
            {
                _logger.debug(String.format(">>>该消息为[通用应答],终端手机号或终端ID:%s,状态信息:%s,EPOCH时间:%s", imeiStr, sessionState, timeStr));
                //应用服务器发送MT给铱星网关确认收到MO的数据
                //01,版本
                //0004,总长度
                //05,MT_CONFIRMATION_ID
                //0001,长度
                //01,接收成功标志
                return "01000405000101";
            }
            /**
             * PAYLOAD
             *
             * 通过铱星网关发送给应用服务器的数据到打报在这里
             */
            //PAYLOAD_ID,例如:02
            String payloadIdStr = strArray[48];
            //字段数据长度,例如:004E
            String payloadLengthStr = strArray[49] + strArray[50];
            int paylaodLength = Integer.parseInt(payloadLengthStr, 16);
            //payload字段的数据
            String[] simpleStrArray = Arrays.copyOfRange(strArray, 51, strArray.length);
            //MSG_ID
            String msgIdStr = simpleStrArray[0] + simpleStrArray[1];
            //铱星设备重启后网关会发一次全部数据,如果平台确认收到MO数据并响应网关,会收到应答信息,这时PAYLOAD部分有变动,需要重新解析,这里根据PAYLOAD里的消息ID判断如何解析
            switch (msgIdStr)
            {
                //位置报告
                case "0200":
                    //SN
                    String sn = simpleStrArray[2] + simpleStrArray[3];
                    //温度
                    String tempperatureStr = simpleStrArray[11];
                    int temperatureNum = 0;
                    //剩余电量
                    int electricityNum = 0;
                    //纬度
                    String latStr = simpleStrArray[12] + simpleStrArray[13] + simpleStrArray[14] + simpleStrArray[15];
                    double latNum = Double.valueOf(Integer.valueOf(latStr, 16)) / 1000000;
                    //经度
                    String lotStr = simpleStrArray[16] + simpleStrArray[17] + simpleStrArray[18] + simpleStrArray[19];
                    double lotNum = Double.valueOf(Integer.valueOf(lotStr, 16)) / 1000000;
                    //海拔
                    String heighStr = simpleStrArray[20] + simpleStrArray[21];
                    int heightNum = Integer.parseInt(heighStr, 16);
                    //速度
                    String speedStr = simpleStrArray[22];
                    int speedNum = Integer.parseInt(heighStr, 16);
                    //方向
                    String dirStr = simpleStrArray[23];
                    int dirNum = Integer.parseInt(heighStr, 16);
                    //终端时间
                    String deviceTime = simpleStrArray[24] + simpleStrArray[25] + simpleStrArray[26] + simpleStrArray[27];
                    Long longDeviceTime = Long.parseLong(deviceTime, 16) * 1000;
                    Date dateTime = new Date(longDeviceTime);
                    String deviceTimeStr = SIMPLEDATEFORMAT.format(dateTime);
                    _logger.debug(String
                            .format(">>>该消息为[位置信息汇报],消息长度:%d,终端手机号或终端ID:%s,状态信息:%s,纬度:%s,经度:%s,海拔:%d," + "温度:%d,电量:%d,终端时间:%s", length,
                                    imeiStr, sessionState, latNum, lotNum, heightNum, temperatureNum, electricityNum, deviceTimeStr));
                    String result = Location(latNum, lotNum, heightNum, imeiStr, "铱星设备", dateTime, temperatureNum, electricityNum);
                    break;
                //通用应答
                case "0001":
                    //流水号
                    String detailStr = simpleStrArray[2] + simpleStrArray[3];
                    //平台消息流水号
                    String detailStr2 = simpleStrArray[4] + simpleStrArray[5];
                    //消息
                    String msgStr = simpleStrArray[6] + simpleStrArray[7];
                    //成功标志
                    String flag = simpleStrArray[8];
                    //上报时间间隔,分钟
                    String upTimeStr = simpleStrArray[9] + simpleStrArray[10];
                    int upTime = Integer.parseInt(upTimeStr, 16);
                    //采样时间间隔,分钟
                    String collectTimeStr = simpleStrArray[11] + simpleStrArray[12];
                    int collectTime = Integer.parseInt(collectTimeStr, 16);
                    _logger.debug(String
                            .format(">>>该消息为[通用应答],消息长度:%d,终端手机号或终端ID:%s,状态信息:%s,终端时间:%s,上报时间间隔:%d分钟," + "采样时间间隔:%d分钟", length, imeiStr,
                                    sessionState, timeStr, upTime, collectTime));
                    break;
                case "8103":
                    _logger.debug(">>>怎么会收到8103的参数ID???");
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            _logger.error(String.format(">>>解析内容时发生异常:%s", e.getMessage()));
        }
        finally
        {
            //应用服务器发送MT给铱星网关确认收到MO的数据
            //01,版本
            //0004,总长度
            //05,MT_CONFIRMATION_ID
            //0001,长度
            //01,接收成功标志
            return "01000405000101";
        }
        //返回"Error..."
        //return "4572726F722E2E2E";
        //返回"Null..."
        //return "4E756C6C2E2E2E";
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

}
