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
     * @param hexStrArray 注册成功返回鉴权码
     * @return
     */
    public String RecevieHexStrArray(String[] hexStrArray)
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
        String typeStr = "";
        for (String tempStr : type)
        {
            typeStr += tempStr;
        }
        //终端ID
        String[] id = Arrays.copyOfRange(hexStrArray, 29, 36);
        String idStr = "";
        for (String tempStr : id)
        {
            idStr += tempStr;
        }
        //车牌颜色
        String[] carColor = Arrays.copyOfRange(hexStrArray, 36, 37);
        //车辆标识(前两位为车牌归属地,后面为车牌号)
        String[] carFlag1 = Arrays.copyOfRange(hexStrArray, 37, 39);
        String[] carFlag2 = Arrays.copyOfRange(hexStrArray, 39, hexStrArray.length);
        //TODO:逻辑处理然后返回鉴权码,这里使用数据库生成的ID作为鉴权码
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setM_deviceName(idStr);
        deviceInfo.setM_type(typeStr);
        deviceInfo.setM_description("我是Socket模拟的Http请求发送过来的");
        JSONObject jsonObject = new JSONObject(deviceInfo);
        String result = new SocketHttp().SendPost("192.168.10.197", 8080, "/Blowdown_Web/DeviceInfo/Insert", jsonObject);
        System.out.println("注册后生成的鉴权码为:" + result);
        return result;
    }

    /**
     * 终端注册应答
     *
     * @param detailArray 应答流水号,对应的终端注册消息的流水号
     * @param akCode      鉴权码,表示注册成功
     * @return
     */
    public String ResponseHexStr(String[] detailArray, String akCode)
    {
        //结果,0:成功1:车辆已被注册2:数据库中无该车辆3:终端已被注册4:数据库中无该终端
        String[] result = new String[]{"1"};
        return "7E 81 00 00 07 55 10 30 00 63 34 12 34 12 34 00 47 41 42 44 A4 7E";
    }

}
