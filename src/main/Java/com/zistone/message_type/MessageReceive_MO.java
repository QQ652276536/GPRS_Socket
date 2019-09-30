package com.zistone.message_type;

import com.alibaba.fastjson.JSON;
import com.zistone.bean.DeviceInfo;
import com.zistone.bean.LocationInfo;
import com.zistone.bean.MessageType;
import com.zistone.socket.SocketHttp;
import com.zistone.util.ConvertUtil;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class MessageReceive_MO
{
    private static SimpleDateFormat SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //Web服务IP
    private static String IP_WEB;
    //Web服务端口
    private static int PORT_WEB;

    static
    {
        IP_WEB = PropertiesUtil.GetValueProperties().getProperty("IP_WEB");
        PORT_WEB = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_WEB"));
    }

    private Logger m_logger = Logger.getLogger(MessageReceive_MO.class);

    /**
     * 位置信息汇报
     *
     * @param lat       纬度
     * @param lot       经度
     * @param height    高度
     * @param tempIdStr 设备编号
     * @param typeStr   设备类型
     * @param timeStr   汇报时间
     * @return
     */
    private String Location(double lat, double lot, double height, String tempIdStr, String typeStr, String timeStr)
    {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setM_deviceId(tempIdStr);
        deviceInfo.setM_lat(lat);
        deviceInfo.setM_lot(lot);
        deviceInfo.setM_height(height);
        deviceInfo.setM_createTime(new Date());
        deviceInfo.setM_updateTime(new Date());
        deviceInfo.setM_state(1);
        deviceInfo.setM_type(typeStr);
        deviceInfo.setM_comment("我是Socket模拟的Http请求发送过来的");
        String deviceJsonStr = JSON.toJSONString(deviceInfo);
        //由Web服务处理终端注册
        String deviceResult = new SocketHttp().SendPost(IP_WEB, PORT_WEB, "/Blowdown_Web/DeviceInfo/InsertByDeviceId", deviceJsonStr);
        int beginIndex = deviceResult.indexOf("{");
        int endIndex = deviceResult.lastIndexOf("}");
        deviceResult = deviceResult.substring(beginIndex, endIndex + 1);
        m_logger.debug(">>>注册铱星设备的返回:" + deviceResult);
        deviceInfo = JSON.parseObject(deviceResult, DeviceInfo.class);

        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setM_deviceId(deviceInfo.getM_deviceId());
        locationInfo.setM_lat(deviceInfo.getM_lat());
        locationInfo.setM_lot(deviceInfo.getM_lot());
        locationInfo.setM_createTime(timeStr);
        String locationStr = JSON.toJSONString(locationInfo);
        //由Web服务处理位置汇报
        String locationResult = new SocketHttp().SendPost(IP_WEB, PORT_WEB, "/Blowdown_Web/LocationInfo/Insert", locationStr);
        int beginIndex2 = locationResult.indexOf("{");
        int endIndex2 = locationResult.lastIndexOf("}");
        locationResult = locationResult.substring(beginIndex2, endIndex2 + 1);
        m_logger.debug(">>>汇报铱星网关位置的返回:" + locationResult);
        locationInfo = JSON.parseObject(locationResult, LocationInfo.class);
        return deviceResult + "◎" + locationResult;
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
            String[] strArray = hexStr.split(" ");
            //版本,例如:01
            String version = strArray[0];
            //总长度,例如:007E
            String totalLength = strArray[1] + strArray[2];
            //MO_HEAD标志,例如:01
            String mo_head_flag = strArray[3];
            //MO_HEAD长度,例如:001C
            String mo_head_length = strArray[4] + strArray[5];
            //消息参考,例如:4C4AF615
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
            //从这里开始是铱星的辅组定位信息
            //LOCATION_ID位置信息头,例如:03
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
            double latNum = Double.valueOf(lat1Str + lat2Str + lat3Str) / 1000000;
            //经度,例如:71DFBB
            String lot1Str = String.valueOf(Integer.parseInt(strArray[41], 16));
            String lot2Str = String.valueOf(Integer.parseInt(strArray[42], 16));
            String lot3Str = String.valueOf(Integer.parseInt(strArray[43], 16));
            double lotNum = Double.valueOf(lot1Str + lot2Str + lot3Str) / 1000000;
            //CEP半径,例如:00000009
            String cep = strArray[44] + strArray[45] + strArray[46] + strArray[47];
            //PAYLOAD_ID头,数据字段,通过铱星网关发送给应用服务器的数据到打报在这里,例如:02
            String payload_id = strArray[48];
            //字段数据长度,例如:004E
            String length2 = strArray[49] + strArray[50];
            //从这里开始是简化协议
            String[] simpleStrArray = Arrays.copyOfRange(strArray, 51, strArray.length);
            //MSG_ID
            String msg_id = simpleStrArray[0] + simpleStrArray[1];
            //SN
            String sn = simpleStrArray[2] + simpleStrArray[3];
            //海拔
            String height = simpleStrArray[20] + simpleStrArray[21];
            int heightNum = Integer.parseInt(height, 16);
            //速度
            String speed = simpleStrArray[22];
            //方向
            String dir = simpleStrArray[23];
            //终端时间
            String deviceTime = simpleStrArray[24] + simpleStrArray[25] + simpleStrArray[26] + simpleStrArray[27];
            Long longDeviceTime = Long.parseLong(deviceTime, 16) * 1000;
            String deviceTimeStr = SIMPLEDATEFORMAT.format(new Date(longDeviceTime));
            m_logger.debug(">>>消息长度:" + length + ",终端手机号或终端ID:" + imeiStr + ",状态信息:" + sessionState + ",纬度:" + latNum + ",经度:" + lotNum + ",终端时间:" + deviceTimeStr);
            String result = Location(latNum, lotNum, heightNum, imeiStr, "铱星设备", deviceTimeStr);
        }
        catch (Exception e)
        {
            m_logger.error(">>>解析内容时发生异常!");
            e.printStackTrace();
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
