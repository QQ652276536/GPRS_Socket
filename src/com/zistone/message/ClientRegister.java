package com.zistone.message;

import com.zistone.bean.DeviceInfo;
import com.zistone.socket.SocketHttp;
import com.zistone.util.ConvertUtil;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * 终端注册
 */
public class ClientRegister
{
    /**
     * 解析终端注册的消息体
     *
     * @param hexStrArray
     * @param idStr       用终端ID作为设备名称
     * @return
     */
    public String RecevieHexStrArray(String[] hexStrArray, String idStr)
    {
        String akCode = null;
        //省域代码
        String[] capital = Arrays.copyOfRange(hexStrArray, 0, 2);
        String provinceStr = "0x" + capital[0] + capital[1];
        int provinceValue = Integer.parseInt(provinceStr.replaceAll("^0[x|X]", ""), 16);
        //市县代码
        String[] city = Arrays.copyOfRange(hexStrArray, 2, 4);
        //制造商
        String[] manufacture = Arrays.copyOfRange(hexStrArray, 4, 9);
        //终端型号
        String[] type = Arrays.copyOfRange(hexStrArray, 9, 29);
        String typeStr = StrArrayToStr(type);
        typeStr = ConvertUtil.HexStrToStr(typeStr);
        //终端ID
        String[] id = Arrays.copyOfRange(hexStrArray, 29, 36);
        String tempIdStr = StrArrayToStr(id);
        tempIdStr = idStr;
        //车牌颜色
        String[] carColor = Arrays.copyOfRange(hexStrArray, 36, 37);
        //车辆标识(前两位为车牌归属地,后面为车牌号)
        String[] carFlag1 = Arrays.copyOfRange(hexStrArray, 37, 39);
        String[] carFlag2 = Arrays.copyOfRange(hexStrArray, 39, hexStrArray.length);
        //由Web服务处理终端注册
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setM_deviceName(tempIdStr);
        deviceInfo.setM_type(typeStr);
        deviceInfo.setM_description("我是Socket模拟的Http请求发送过来的");
        JSONObject jsonObject = new JSONObject(deviceInfo);
        String result = new SocketHttp().SendPost("192.168.10.197", 8080, "/Blowdown_Web/DeviceInfo/Insert", jsonObject);
        System.out.println(">>>终端注册后返回的内容:" + result);
        return result;
    }

    /**
     * 终端注册应答
     *
     * @param result 结果,这里的结果来自Web服务,需要再次判断
     * @return
     */
    public String ResponseHexStr(String result)
    {
        String responseStr = ConvertUtil.HexStrToStr("7E");
        //响应终端注册的消息ID的十进制
        responseStr += "33024";
        //结果,0:成功1:车辆已被注册2:数据库中无该车辆3:终端已被注册4:数据库中无该终端
        switch (result)
        {
            //设备添加失败,该设备名已存在
            case "-1":
                responseStr += "3";
                break;
            //设备添加失败,未知错误
            case "-2":
                responseStr += "4";
                break;
            //注册成功
            default:
                responseStr += "0";
                break;
        }
        //鉴权码
        responseStr += "success";
        responseStr += ConvertUtil.HexStrToStr("7E");
        System.out.println(responseStr);
        return responseStr;
        //~330240success~
        //7E8100000D2340602159701A0B1A0A006A616D65732D64656D6FD87E
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
