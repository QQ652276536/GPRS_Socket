package com.zistone.message_type;

import com.alibaba.fastjson.JSON;
import com.zistone.bean.DeviceInfo;
import com.zistone.socket.SocketHttp;
import com.zistone.util.ConvertUtil;

/**
 * 终端鉴权
 */
public class ClientAuthentication
{
    /**
     * 解析消息体
     *
     * @param hexStrArray 鉴权码
     * @return
     */
    public String RecevieHexStrArray(String[] hexStrArray)
    {
        String hexAKCode = ConvertUtil.StrArrayToStr(hexStrArray);
        String akCode = ConvertUtil.HexStrToStr(hexAKCode);
        //由Web服务处理终端鉴权
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setM_akCode(akCode);
        String jsonStr = JSON.toJSONString(deviceInfo);
        String result = new SocketHttp().SendPost("192.168.10.197", 8080, "/Blowdown_Web/DeviceInfo/FindAKCode", jsonStr);
        System.out.println(">>>终端鉴权返回的内容:" + result);
        return result;
    }

    /**
     * 生成响应内容
     *
     * @param detailStr 消息流水
     * @param result
     * @return
     */
    public String ResponseHexStr(String detailStr, String result)
    {
        String responseStr = ConvertUtil.HexStrToStr("7E");
        //应答ID,对应终端消息的ID
        responseStr += "258";
        //应答流水号,对应终端消息的流水号
        responseStr += detailStr;
        int beginIndex = result.indexOf("GMT");
        int endIndex = result.indexOf("}");
        result = result.substring(beginIndex + 3, endIndex);
        //结果,0:成功1:失败2:2消息有误3:不支持4:报警处理确认
        switch (result)
        {
            //成功
            case "1":
                responseStr += "0";
                break;
            //失败
            default:
                responseStr += "1";
                break;
        }
        responseStr += ConvertUtil.HexStrToStr("7E");
        System.out.println(">>>终端鉴权响应的内容:" + responseStr);
        return responseStr;
    }
}
