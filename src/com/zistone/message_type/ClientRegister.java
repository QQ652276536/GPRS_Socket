package com.zistone.message_type;

import com.alibaba.fastjson.JSON;
import com.zistone.bean.DeviceInfo;
import com.zistone.socket.SocketHttp;
import com.zistone.util.ConvertUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Random;

/**
 * 终端注册
 */
public class ClientRegister
{
    private static Logger LOG = Logger.getLogger(ClientRegister.class);

    private String m_ip;
    private int m_port;

    public ClientRegister(String ip, int port)
    {
        m_ip = ip;
        m_port = port;
    }

    /**
     * 解析消息体
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
        String provinceStr = ConvertUtil.StrArrayToStr(capital);
        int provinceValue = Integer.parseInt(provinceStr.replaceAll("0[x|X]", ""), 16);
        //市县代码
        String[] city = Arrays.copyOfRange(hexStrArray, 2, 4);
        //制造商
        String[] manufacture = Arrays.copyOfRange(hexStrArray, 4, 9);
        //终端型号
        String[] type = Arrays.copyOfRange(hexStrArray, 9, 29);
        String typeStr = "";
        //去除补位的零
        for (String tempStr : type)
        {
            if (!"00".equals(tempStr))
            {
                typeStr += tempStr;
            }
        }
        typeStr = ConvertUtil.HexStrToStr(typeStr);
        //终端ID
        String[] id = Arrays.copyOfRange(hexStrArray, 29, 36);
        String tempIdStr = ConvertUtil.StrArrayToStr(id);
        tempIdStr = idStr;
        //车牌颜色
        String[] carColor = Arrays.copyOfRange(hexStrArray, 36, 37);
        //车辆标识(前两位为车牌归属地,后面为车牌号)
        String[] carFlag1 = Arrays.copyOfRange(hexStrArray, 37, 39);
        String[] carFlag2 = Arrays.copyOfRange(hexStrArray, 39, hexStrArray.length);
        //由Web服务处理终端注册
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setM_state(1);

        //TODO:测试用,上线的时候记得删掉,因为设备名是唯一的
        Random random = new Random();
        byte[] randomBytes = new byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 0};
        random.nextBytes(randomBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : randomBytes)
        {
            sb.append(Math.abs(Byte.valueOf(b).intValue()) % 10);
        }
        LOG.debug(">>>随机生成的设备名的后缀是:" + sb.toString());

        deviceInfo.setM_name(tempIdStr + sb.toString());
        deviceInfo.setM_type(typeStr);
        deviceInfo.setM_description("我是Socket模拟的Http请求发送过来的");
        String jsonStr = JSON.toJSONString(deviceInfo);
        String result = new SocketHttp().SendPost(m_ip, m_port, "/Blowdown_Web/DeviceInfo/Insert", jsonStr);
        LOG.debug(">>>终端注册返回:" + result);
        return result;
    }

    /**
     * 生成响应内容
     *
     * @param result 结果,这里的结果来自Web服务,需要再次判断
     * @return
     */
    public DeviceInfo ResponseHexStr(String result)
    {
        int beginIndex = result.indexOf("{");
        int endIndex = result.indexOf("}") + 1;
        result = result.substring(beginIndex, endIndex);
        DeviceInfo deviceInfo = JSON.parseObject(result, DeviceInfo.class);
        return deviceInfo;
    }

}
