package com.zistone.message;

import com.zistone.bean.MessageType;
import com.zistone.util.ConvertUtil;

import java.math.BigInteger;
import java.util.Arrays;

public class MessageReceive
{
    /**
     * 解析终端发送过来的16进制的Str
     *
     * @param hexStr
     * @return
     */
    public String RecevieHexStr(String hexStr)
    {
        String[] strArray = hexStr.split(" ");
        //前后两个标识位
        String flag1 = strArray[0];
        String flag2 = strArray[strArray.length - 1];
        //校验码
        String checkCode = strArray[strArray.length - 2];
        //消息头,包含消息ID、消息体属性、手机号、消息流水
        String[] tempArray = new String[strArray.length - 3];
        System.arraycopy(strArray, 1, tempArray, 0, tempArray.length);
        String[] headArray = Arrays.copyOfRange(tempArray, 0, 12);
        //消息ID
        String[] idArray = Arrays.copyOfRange(headArray, 0, 2);
        String idStr = "0x" + idArray[0] + idArray[1];
        //消息体属性
        String[] bodyPropertyArray = Arrays.copyOfRange(headArray, 2, 4);
        String bodyPropertyStr = "0x" + bodyPropertyArray[0] + bodyPropertyArray[1];
        //终端手机号
        String[] phoneArray = Arrays.copyOfRange(headArray, 4, 10);
        String phoneStr = "";
        for (String tempStr : phoneArray)
        {
            phoneStr += new BigInteger(tempStr,16).toString();
        }
        //消息流水号
        String[] detailArray = Arrays.copyOfRange(headArray, 10, 12);
        String detailStr = "0x" + detailArray[0] + detailArray[1];
        //消息体,不同消息ID对应不同的消息体结构
        String[] bodyArray = Arrays.copyOfRange(tempArray, 12, tempArray.length);
        //根据消息ID判断消息类型
        int idValue = Integer.parseInt(idStr.replaceAll("^0[x|X]", ""), 16);
        ClientRegister clientRegister = new ClientRegister();
        switch (idValue)
        {
            //终端注册
            case MessageType.CLIENTREGISTER:
                //有鉴权码则代表成功
                String akCode = clientRegister.RecevieHexStrArray(bodyArray);
                if (null != akCode)
                {
                    //终端注册应答
                    return clientRegister.ResponseHexStr(detailArray, akCode);
                }
                break;
            //位置信息汇报
            case MessageType.LOCATIONREPORT:
                break;
            default:
                break;
        }
        //错误消息ID就返回空
        return "";
    }
}
