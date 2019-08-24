package com.zistone.message_type;

import com.alibaba.fastjson.JSON;
import com.zistone.bean.DeviceInfo;
import com.zistone.socket.SocketHttp;
import com.zistone.util.ConvertUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * 位置信息汇报
 */
public class ClientLocation
{
    private static Logger LOG = Logger.getLogger(ClientLocation.class);

    private String m_ip;
    private int m_port;

    public ClientLocation(String ip, int port)
    {
        m_ip = ip;
        m_port = port;
    }

    /**
     * 解析消息体
     *
     * @param deviceInfo
     * @param hexStrArray
     * @return
     */
    public String RecevieHexStrArray(DeviceInfo deviceInfo, String[] hexStrArray)
    {
        //报警标志
        String[] warningFlag = Arrays.copyOfRange(hexStrArray, 0, 4);
        String warningStr = ConvertUtil.StrArrayToStr(warningFlag);
        //状态
        String[] state = Arrays.copyOfRange(hexStrArray, 4, 8);
        String stateStr = ConvertUtil.StrArrayToStr(state);
        //纬度
        String[] lat = Arrays.copyOfRange(hexStrArray, 8, 12);
        String latStr = ConvertUtil.StrArrayToStr(lat);
        byte[] latBytes = ConvertUtil.HexStrToByteArray(latStr);
        double latNum = (double) ConvertUtil.ByteArray4ToLong(latBytes) / 1000000;
        //经度
        String[] lot = Arrays.copyOfRange(hexStrArray, 12, 16);
        String lotStr = ConvertUtil.StrArrayToStr(lot);
        byte[] lotBytes = ConvertUtil.HexStrToByteArray(lotStr);
        double lotNum = (double) ConvertUtil.ByteArray4ToLong(lotBytes) / 1000000;
        //海拔
        String[] height = Arrays.copyOfRange(hexStrArray, 16, 18);
        String heightStr = ConvertUtil.StrArrayToStr(height);
        byte[] heightBytes = ConvertUtil.HexStrToByteArray(heightStr);
        double heightNum = ConvertUtil.ByteArray2ToInt(heightBytes);
        //由Web服务处理位置汇报
        deviceInfo.setM_lat(latNum);
        deviceInfo.setM_lot(lotNum);
        deviceInfo.setM_height(heightNum);
        String jsonStr = JSON.toJSONString(deviceInfo);
        String result = new SocketHttp().SendPost(m_ip, m_port, "/Blowdown_Web/DeviceInfo/UpdateByName", jsonStr);
        LOG.debug(">>>位置汇报返回:" + result);
        return result;
    }

    /**
     * 生成响应内容
     *
     * @param result
     * @return 受影响行数
     */
    public String ResponseHexStr(String result)
    {
        int beginIndex = result.indexOf("GMT");
        int endIndex = result.indexOf("}");
        result = result.substring(beginIndex + 3, endIndex);
        return result;
    }
}
