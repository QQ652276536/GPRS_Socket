package com.zistone.message_type;

import com.alibaba.fastjson.JSON;
import com.zistone.bean.DeviceInfo;
import com.zistone.bean.LocationInfo;
import com.zistone.socket.SocketHttp;
import com.zistone.util.ConvertUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * 位置信息汇报
 */
public class ClientLocation
{
    private Logger m_logger = Logger.getLogger(ClientLocation.class);

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
        //速度
        String[] speed = Arrays.copyOfRange(hexStrArray, 18, 20);
        String speedStr = ConvertUtil.StrArrayToStr(speed);
        byte[] speedBytes = ConvertUtil.HexStrToByteArray(speedStr);
        double speedNum = ConvertUtil.ByteArray2ToInt(speedBytes);
        //方向
        String[] dir = Arrays.copyOfRange(hexStrArray, 20, 22);
        String dirStr = ConvertUtil.StrArrayToStr(dir);
        byte[] dirBytes = ConvertUtil.HexStrToByteArray(dirStr);
        int dirNum = ConvertUtil.ByteArray2ToInt(dirBytes);
        //时间
        String[] time = Arrays.copyOfRange(hexStrArray, 22, 28);
        String year = "20" + time[0];
        String month = String.valueOf(time[1]).replace("0", "");
        String day = time[2];
        String hour = time[3];
        String minute = time[4];
        String second = time[5];
        String timeStr = year + "/" + month + "/" + day + " " + hour + ":" + minute + ":" + second;
        //由Web服务处理位置汇报
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setM_deviceId(deviceInfo.getM_deviceId());
        locationInfo.setM_lat(deviceInfo.getM_lat());
        locationInfo.setM_lot(deviceInfo.getM_lot());
        locationInfo.setM_createTime(timeStr);
        String jsonStr = JSON.toJSONString(locationInfo);
        String result = new SocketHttp().SendPost(m_ip, m_port, "/Blowdown_Web/LocationInfo/Insert", jsonStr);
        m_logger.debug(">>>位置汇报返回:" + result);
        return result;
    }

    /**
     * @param result
     * @return
     */
    public LocationInfo ResponseHexStr(String result)
    {
        int beginIndex = result.indexOf("GMT");
        result = result.substring(beginIndex + 3);
        beginIndex = result.indexOf("{");
        result = result.substring(beginIndex);
        return JSON.parseObject(result, LocationInfo.class);
    }
}
