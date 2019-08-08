package com.zistone.message_type;

import com.zistone.bean.MessageType;
import com.zistone.util.ConvertUtil;

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
        //若校验码、消息头、消息体出现0x7e(~字符)则要进行转义处理,0x7e<--->0x7d0x02,0x7d<-->0x7d0x01
        for (int i = 0; i < strArray.length; i++)
        {
            //前后两个标识不转义
            if (i == 0 || i == strArray.length - 1)
            {
                continue;
            }
            else if (strArray[i].equals("7e"))
            {
                strArray[i] = "7d 02";
                System.out.println(">>>消息中有需要转义的字符!!!");
            }
        }
        //校验码
        String checkCode = strArray[strArray.length - 2];
        //消息头,包含消息ID、消息体属性、手机号、消息流水
        String[] tempArray = new String[strArray.length - 3];
        System.arraycopy(strArray, 1, tempArray, 0, tempArray.length);
        String[] headArray = Arrays.copyOfRange(tempArray, 0, 12);
        //消息ID
        String[] idArray = Arrays.copyOfRange(headArray, 0, 2);
        String idStr = "0x" + ConvertUtil.StrArrayToStr(idArray);
        int idValue = Integer.parseInt(idStr.replaceAll("^0[x|X]", ""), 16);
        //消息体属性
        String[] bodyPropertyArray = Arrays.copyOfRange(headArray, 2, 4);
        String bodyPropertyStr = "0x" + ConvertUtil.StrArrayToStr(bodyPropertyArray);
        //终端手机号(根据对应的测试工具测出来结果为终端ID)
        String[] phoneArray = Arrays.copyOfRange(headArray, 4, 10);
        String phoneStr = ConvertUtil.StrArrayToStr(phoneArray);
        //消息流水号
        String[] detailArray = Arrays.copyOfRange(headArray, 10, 12);
        String detailStr = "0x" + ConvertUtil.StrArrayToStr(detailArray);
        //消息体,不同消息ID对应不同的消息体结构
        String[] bodyArray = Arrays.copyOfRange(tempArray, 12, tempArray.length);
        //根据消息ID判断消息类型
        switch (idValue)
        {
            //终端心跳,消息体为空
            case MessageType.CLIENTHEARTBEAT:
            {
                return "";
            }
            //终端鉴权
            case MessageType.CLIENTAK:
            {
                ClientAuthentication clientAuthentication = new ClientAuthentication();
                String result = clientAuthentication.RecevieHexStrArray(bodyArray);
                //平台通用应答
                return clientAuthentication.ResponseHexStr(detailStr, result);
            }
            //终端注册
            case MessageType.CLIENTREGISTER:
            {
                ClientRegister clientRegister = new ClientRegister();
                String result = clientRegister.RecevieHexStrArray(bodyArray, phoneStr);
                //终端注册应答
                return clientRegister.ResponseHexStr(result);
            }
            //位置信息汇报
            case MessageType.LOCATIONREPORT:
            {
                ClientLocation clientLocation = new ClientLocation();
                String result = clientLocation.RecevieHexStrArray(bodyArray);
                //位置汇报应答
                return clientLocation.ResponseHexStr(detailStr, result);
            }
            default:
                break;
        }
        //错误消息ID就返回空
        return "";
    }

}
