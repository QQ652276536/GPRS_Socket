package com.zistone.message_type;

import com.alibaba.fastjson.JSON;
import com.zistone.bean.DeviceInfo;
import com.zistone.socket.SocketHttp;
import com.zistone.util.ConvertUtil;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

/**
 * 终端鉴权
 */
public class ClientAuthentication
{
    private static Logger LOG = Logger.getLogger(ClientAuthentication.class);
    private static String IP;

    public ClientAuthentication()
    {
        IP = PropertiesUtil.GetValueProperties().getProperty("IP");
    }

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
        String result = new SocketHttp().SendPost(IP, 8080, "/Blowdown_Web/DeviceInfo/FindAKCode", jsonStr);
        LOG.debug(">>>终端鉴权返回:" + result);
        return result;
    }

    /**
     * 生成响应内容
     *
     * @param detailStr 消息流水
     * @param result
     * @return 鉴权码
     */
    public String ResponseHexStr(String detailStr, String result)
    {
        int beginIndex = result.indexOf("GMT");
        int endIndex = result.indexOf("}");
        result = result.substring(beginIndex + 3, endIndex);
        return result;
    }
}
