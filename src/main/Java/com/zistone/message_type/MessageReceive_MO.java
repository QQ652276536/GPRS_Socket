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
     * 解析终端发送过来的16进制的Str
     *
     * @param hexStr
     * @return
     */
    public String RecevieHexStr(String hexStr)
    {
        //"解析出来是'Error...'"
        String responseStr = "4572726F722E2E2E";
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
            //经度,例如:0157EFBC
            String lot = strArray[72] + strArray[73] + strArray[74] + strArray[75];
            //纬度,例如:06CAA233
            String lat = strArray[76] + strArray[77] + strArray[78] + strArray[79];
            //高度,例如:000C
            String height = strArray[80] + strArray[81];
            //速度,例如:0000
            String speed = strArray[82] + strArray[83];
            //方向,例如:0000
            String dir = strArray[84] + strArray[85];
            //时间,例如:190103190852
            String time = strArray[86] + strArray[87] + strArray[88] + strArray[89] + strArray[90] + strArray[91];
            //附加消息:01040000000030024102310100F11000000000000000000000000000000000F2020000
            //检验标志,例如:EF
            String checkCode = strArray[strArray.length - 2];
            //标识,例如:7E
            String flag2 = strArray[strArray.length - 1];
            m_logger.debug(">>>校验码:" + checkCode + ",消息ID:" + messagId + ",消息体属性:" + bodyPropertyStr + ",终端手机号或终端ID:" + imei_bcd + "," +
                    "消息流水:" + detail);

            //应用服务器发送MT给铱星网关确认收到MO的数据
            //01,版本
            //0004,总长度
            //05,MT_CONFIRMATION_ID
            //0001,长度
            //01,接收成功标志
            responseStr = "01000405000101";
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return responseStr;
        }
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
