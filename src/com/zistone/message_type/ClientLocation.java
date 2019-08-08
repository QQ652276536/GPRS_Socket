package com.zistone.message_type;

import com.alibaba.fastjson.JSON;
import com.zistone.bean.DeviceInfo;
import com.zistone.socket.SocketHttp;
import com.zistone.util.ConvertUtil;

import java.util.Arrays;

/**
 * 位置信息汇报
 */
public class ClientLocation
{
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
        String stateStr = ConvertUtil.StrArrayToStr(warningFlag);
        //纬度
        String[] lat = Arrays.copyOfRange(hexStrArray, 8, 12);
        String latStr = ConvertUtil.StrArrayToStr(warningFlag);
        //经度
        String[] lot = Arrays.copyOfRange(hexStrArray, 12, 16);
        String lotStr = ConvertUtil.StrArrayToStr(warningFlag);
        //海拔
        String[] height = Arrays.copyOfRange(hexStrArray, 16, 18);
        String heightStr = ConvertUtil.StrArrayToStr(warningFlag);
        //由Web服务处理位置汇报
        deviceInfo.setM_lat(31.245105);
        deviceInfo.setM_lot(121.506377);
        deviceInfo.setM_height(100);
        String jsonStr = JSON.toJSONString(deviceInfo);
        String result = new SocketHttp().SendPost("192.168.10.197", 8080, "/Blowdown_Web/DeviceInfo/UpdateByName", jsonStr);
        System.out.println(">>>位置汇报返回:" + result);
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
