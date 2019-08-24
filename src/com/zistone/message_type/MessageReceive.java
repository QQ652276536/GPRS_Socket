package com.zistone.message_type;

import com.zistone.bean.DeviceInfo;
import com.zistone.bean.MessageType;
import com.zistone.util.ConvertUtil;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;

public class MessageReceive
{
    private static Logger LOG = Logger.getLogger(MessageReceive.class);
    private static String IP;
    private static int PORT;

    private DeviceInfo m_deviceInfo;

    public MessageReceive()
    {
        IP = PropertiesUtil.GetValueProperties().getProperty("IP");
        PORT = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT"));
    }

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
        //        for (int i = 0; i < strArray.length; i++)
        //        {
        //            //前后两个标识不转义
        //            if (i == 0 || i == strArray.length - 1)
        //            {
        //                continue;
        //            }
        //            else if (strArray[i].equals("7e"))
        //            {
        //                strArray[i] = "7d 02";
        //                Log.debug(">>>消息中有需要转义的字符!!!");
        //            }
        //        }
        //校验码
        String checkCode = strArray[strArray.length - 2];
        //消息头,包含消息ID、消息体属性、手机号、消息流水
        String[] tempArray = new String[strArray.length - 3];
        System.arraycopy(strArray, 1, tempArray, 0, tempArray.length);
        String[] headArray = Arrays.copyOfRange(tempArray, 0, 12);
        //消息ID
        String[] idArray = Arrays.copyOfRange(headArray, 0, 2);
        String idStr = ConvertUtil.StrArrayToStr(idArray);
        int idValue = Integer.parseInt(idStr.replaceAll("0[x|X]", ""), 16);
        //消息体属性
        String[] bodyPropertyArray = Arrays.copyOfRange(headArray, 2, 4);
        String bodyPropertyStr = ConvertUtil.StrArrayToStr(bodyPropertyArray);
        //终端手机号(根据对应的测试工具测出来结果为终端ID)
        String[] phoneArray = Arrays.copyOfRange(headArray, 4, 10);
        String phoneStr = ConvertUtil.StrArrayToStr(phoneArray);
        //消息流水号
        String[] detailArray = Arrays.copyOfRange(headArray, 10, 12);
        String detailStr = ConvertUtil.StrArrayToStr(detailArray);
        //消息体,不同消息ID对应不同的消息体结构
        String[] bodyArray = Arrays.copyOfRange(tempArray, 12, tempArray.length);
        //根据消息ID判断消息类型
        switch (idValue)
        {
            //终端注册
            case MessageType.CLIENTREGISTER:
            {
                ClientRegister clientRegister = new ClientRegister(IP, PORT);
                String result = clientRegister.RecevieHexStrArray(bodyArray, phoneStr);
                m_deviceInfo = clientRegister.ResponseHexStr(result);
                //终端注册应答（0x8100）
                String responseStr = "7E";
                //应答流水号,对应终端注册消息的流水号
                responseStr += "8100";
                if (null != m_deviceInfo)
                {
                    String akCode = m_deviceInfo.getM_akCode();
                    //结果,0:成功1:车辆已被注册2:数据库中无该车辆3:终端已被注册4:数据库中无该终端
                    if (null != akCode && !"".equals(akCode))
                    {
                        responseStr += "00";
                    }
                    else
                    {
                        responseStr += "03";
                    }
                    responseStr += ConvertUtil.StrToHexStr(akCode);
                }
                responseStr += "7E";
                LOG.debug(">>>终端注册响应:" + responseStr);
                return responseStr;
            }
            //位置信息汇报
            case MessageType.LOCATIONREPORT:
            {
                //需要先鉴权,即判断设备
                if (null != m_deviceInfo && null != m_deviceInfo.getM_akCode() && !"".equals(m_deviceInfo.getM_akCode()))
                {
                    ClientLocation clientLocation = new ClientLocation(IP, PORT);
                    String result = clientLocation.RecevieHexStrArray(m_deviceInfo, bodyArray);
                    String line = clientLocation.ResponseHexStr(result);
                    //平台通用应答(0x8001)
                    String responseStr = "7E";
                    //应答ID,对应终端消息的ID
                    responseStr += "8001";
                    //应答流水号,对应终端消息的流水号
                    responseStr += detailStr;
                    //结果,0:成功1:失败2:2消息有误3:不支持4:报警处理确认
                    if ("1".equals(line))
                    {
                        responseStr += "00";
                    }
                    else
                    {
                        responseStr += "01";
                    }
                    responseStr += "7E";
                    LOG.debug(">>>位置信息汇报响应:" + responseStr);
                    return responseStr;
                }
                else
                {
                    LOG.error(">>>鉴权失败,请确认鉴权无误!");
                    break;
                }
            }
            //终端鉴权
            case MessageType.CLIENTAK:
            {
                ClientAuthentication clientAuthentication = new ClientAuthentication(IP, PORT);
                String result = clientAuthentication.RecevieHexStrArray(bodyArray);
                String akCode = clientAuthentication.ResponseHexStr(detailStr, result);
                //平台通用应答(0x8001)
                String responseStr = "7E";
                //应答ID,对应终端消息的ID
                responseStr += "8001";
                //应答流水号,对应终端消息的流水号
                responseStr += detailStr;
                //结果,0:成功1:失败2:2消息有误3:不支持4:报警处理确认
                if (null != m_deviceInfo && m_deviceInfo.getM_akCode() == akCode)
                {
                    responseStr += "00";
                }
                else
                {
                    responseStr += "01";
                }
                responseStr += "7E";
                LOG.debug(">>>终端鉴权响应:" + responseStr);
                return responseStr;
            }
            //终端心跳,消息体为空
            case MessageType.CLIENTHEARTBEAT:
            {
                return "";
            }
            default:
                break;
        }
        //错误消息ID就返回空
        return "";
    }

}
