package com.zistone.message_type;

import com.alibaba.fastjson.JSON;
import com.zistone.bean.DeviceInfo;
import com.zistone.socket.SocketHttp;
import com.zistone.util.ConvertUtil;
import org.apache.log4j.Logger;

/**
 * 终端鉴权
 */
public class ClientAuthentication
{
    private Logger m_logger = Logger.getLogger(ClientAuthentication.class);

    private String m_ip;
    private int m_port;

    public ClientAuthentication(String ip, int port)
    {
        m_ip = ip;
        m_port = port;
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
        String result = new SocketHttp().SendPost(m_ip, m_port, "/Blowdown_Web/DeviceInfo/FindByAKCode", jsonStr);
        m_logger.debug(">>>终端鉴权返回:" + result);
        return result;
    }

    /**
     * @param result
     * @return 鉴权码
     */
    public DeviceInfo ResponseHexStr(String result)
    {
        result = result.substring(result.indexOf("{"));
        return JSON.parseObject(result, DeviceInfo.class);
    }
}
