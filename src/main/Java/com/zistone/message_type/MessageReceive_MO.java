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

    /**
     * 位置信息汇报
     *
     * @param lat       纬度
     * @param lot       经度
     * @param tempIdStr 设备编号
     * @param typeStr   设备类型
     * @param timeStr   汇报时间
     * @return
     */
    private String Location(double lat, double lot, String tempIdStr, String typeStr, String timeStr)
    {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setM_deviceId(tempIdStr);
        deviceInfo.setM_lat(lat);
        deviceInfo.setM_lot(lot);
        deviceInfo.setM_createTime(new Date());
        deviceInfo.setM_updateTime(new Date());
        deviceInfo.setM_state(1);
        deviceInfo.setM_type(typeStr);
        deviceInfo.setM_comment("我是Socket模拟的Http请求发送过来的");
        String deviceJsonStr = JSON.toJSONString(deviceInfo);
        //由Web服务处理设备更新
        String deviceResult = new SocketHttp().SendPost(IP_WEB, PORT_WEB, "/Blowdown_Web/DeviceInfo/InsertByDeviceId", deviceJsonStr);
        int beginIndex = deviceResult.indexOf("{");
        int endIndex = deviceResult.lastIndexOf("}");
        deviceResult = deviceResult.substring(beginIndex, endIndex + 1);
        m_logger.debug(">>>汇报铱星网关位置的返回:" + deviceResult);
        deviceInfo = JSON.parseObject(deviceResult, DeviceInfo.class);
        if (deviceInfo == null)
        {
            return deviceResult;
        }
        else
        {
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
            //消息参考,例如:ID4C4AF615
            String messageId = strArray[6] + strArray[7] + strArray[8] + strArray[9];
            //IMEI,例如:33 30 30 32 33 34 30 36 30 32 31 35 39 37 30
            String imei =
                    strArray[10] + strArray[11] + strArray[12] + strArray[13] + strArray[14] + strArray[15] + strArray[16] + strArray[17] + strArray[18] + strArray[19] + strArray[20] + strArray[21] + strArray[22] + strArray[23] + strArray[24];
            //会话状态,例如:成功00
            String sessionState = strArray[25];
            //MO_MSN,例如:01EA
            String mo_msn = strArray[26] + strArray[27];
            //MT_MSN,例如:0000
            String mt_msn = strArray[28] + strArray[29];
            //EPOCH时间,例如:5C2DED4D
            String epoch_time = strArray[30] + strArray[31] + strArray[32] + strArray[33];

            //从这里开始是铱星的辅组定位信息,例如:可以不管
            //LOCATION_ID位置信息头,例如:03
            String locationHead_yx = strArray[34];
            //长度,例如:000B
            String locationLength_yx = strArray[35] + strArray[36];
            //定位状态,例如:00
            String locationState_yx = strArray[37];
            //纬度,例如:167D12
            String lat_yx = strArray[38] + strArray[39] + strArray[40];
            //经度,例如:71DFBB
            String lot_yx = strArray[41] + strArray[42] + strArray[43];
            //CEP半径,例如:00000009
            String cep_yx = strArray[44] + strArray[45] + strArray[46] + strArray[47];
            //PAYLOAD_ID头,数据字段,通过铱星网关发送给应用服务器的数据到打报在这里,例如:02
            String payload_id_yx = strArray[48];
            //字段数据长度,例如:004E
            String length_yx = strArray[49] + strArray[50];

            //从这里开始就是部标协议了
            //头标识,例如:7E
            String flag1 = strArray[51];
            //MSG_ID, 0X0200, 终端位置报告,例如:0200
            String messagId = strArray[52] + strArray[53];
            //消息体数据长度,例如:003F
            String bodyPropertyStr = strArray[54] + strArray[55];
            //IMEI的后12位BCD码，6字节,例如:234060215970
            String imei_bcd = strArray[56] + strArray[57] + strArray[58] + strArray[59] + strArray[60] + strArray[61];
            //流水号,例如:199B
            String detail = strArray[62] + strArray[63];
            //告警信息,例如:00000000
            String warning = strArray[64] + strArray[65] + strArray[66] + strArray[67];
            //状态信息,例如:00000002
            String state = strArray[68] + strArray[69] + strArray[70] + strArray[71];
            //纬度,例如:0157EFBC
            String lat = strArray[72] + strArray[73] + strArray[74] + strArray[75];
            byte[] latBytes = ConvertUtil.HexStrToByteArray(lat);
            double latNum = (double) ConvertUtil.ByteArray4ToLong(latBytes) / 1000000;
            //经度,例如:06CAA233
            String lot = strArray[76] + strArray[77] + strArray[78] + strArray[79];
            byte[] lotBytes = ConvertUtil.HexStrToByteArray(lot);
            double lotNum = (double) ConvertUtil.ByteArray4ToLong(lotBytes) / 1000000;
            //高度,例如:000C
            String height = strArray[80] + strArray[81];
            //速度,例如:0000
            String speed = strArray[82] + strArray[83];
            //方向,例如:0000
            String dir = strArray[84] + strArray[85];
            //时间,例如:190103190852
            String time = strArray[86] + strArray[87] + strArray[88] + strArray[89] + strArray[90] + strArray[91];
            String year = strArray[86].equals("00") ? "0000" : "20" + strArray[86];
            String month = strArray[87].equals("00") ? "00" : strArray[87].replace("0", "");
            String day = strArray[88];
            String hour = strArray[89];
            String minute = strArray[90];
            String second = strArray[91];
            String timeStr = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
            //附加消息:01040000000030024102310100F11000000000000000000000000000000000F2020000
            //检验标志,例如:EF
            String checkCode = strArray[strArray.length - 2];
            //标识,例如:7E
            String flag2 = strArray[strArray.length - 1];
            m_logger.debug(">>>校验码:" + checkCode + ",消息ID:" + messagId + ",消息体属性:" + bodyPropertyStr + ",终端手机号或终端ID:" + imei_bcd + "," +
                    "消息流水:" + detail + ",告警信息:" + warning + ",状态信息:" + state + ",纬度:" + latNum + ",经度:" + lotNum);
            String result = Location(latNum, lotNum, imei_bcd, "铱星设备", timeStr);
            //应用服务器发送MT给铱星网关确认收到MO的数据
            //01,版本
            //0004,总长度
            //05,MT_CONFIRMATION_ID
            //0001,长度
            //01,接收成功标志
        }
        catch (Exception e)
        {
            m_logger.error(">>>解析内容时发生异常!");
            e.printStackTrace();
        }
        finally
        {
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
