package com.zistone.message;

import com.zistone.bean.DeviceInfo;
import com.zistone.socket.SocketHttp;
import com.zistone.util.ConvertUtil;
import org.json.JSONObject;

import java.util.Arrays;

public class ClientLocation
{
    public String RecevieHexStrArray(String[] hexStrArray)
    {
        //报警标志
        String[] warningFlag = Arrays.copyOfRange(hexStrArray, 0, 4);
        String warningStr = StrArrayToStr(warningFlag);
        //状态
        String[] state = Arrays.copyOfRange(hexStrArray, 4, 8);
        String stateStr = StrArrayToStr(warningFlag);
        //纬度
        String[] lat = Arrays.copyOfRange(hexStrArray, 8, 12);
        String latStr = StrArrayToStr(warningFlag);
        //经度
        String[] lot = Arrays.copyOfRange(hexStrArray, 12, 16);
        String lotStr = StrArrayToStr(warningFlag);
        //海拔
        String[] height = Arrays.copyOfRange(hexStrArray, 16, 18);
        String heightStr = StrArrayToStr(warningFlag);
        //由Web服务处理位置汇报
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setM_lat(31.245105);
        deviceInfo.setM_lot(121.506377);
        deviceInfo.setM_height(100);
        JSONObject jsonObject = new JSONObject(deviceInfo);
        String result = new SocketHttp().SendPost("192.168.10.197", 8080, "/Blowdown_Web/DeviceInfo/UpdateByName", jsonObject);
        System.out.println(">>>位置汇报后返回的内容:" + result);
        return result;
    }

    public String ResponseHexStr(String detailStr, String result)
    {
        return "~success~";
    }

    public String StrArrayToStr(String[] strArray)
    {
        String str = "";
        for (String tempStr : strArray)
        {
            str += tempStr;
        }
        return str;
    }
}
