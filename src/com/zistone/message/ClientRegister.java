package com.zistone.message;

import java.util.Arrays;

/**
 * 终端注册
 */
public class ClientRegister
{
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
        //终端ID
        String[] id = Arrays.copyOfRange(hexStrArray, 29, 36);
        //车牌颜色
        String[] carColor = Arrays.copyOfRange(hexStrArray, 36, 37);
        //车辆标识
        String[] carFlag = Arrays.copyOfRange(hexStrArray, 37, hexStrArray.length);
        //TODO:逻辑处理然后返回
        //终端注册应答

        return akCode;
    }

    /**
     * 终端注册应答
     *
     * @param detailArray 应答流水号,对应的终端注册消息的流水号
     * @param akCode      鉴权码,只有在成功后才有该字段
     * @return
     */
    public String ResponseHexStr(String[] detailArray, String akCode)
    {
        //结果,0:成功1:车辆已被注册2:数据库中无该车辆3:终端已被注册4:数据库中无该终端
        String[] result = new String[]{"1"};
        return "7E 81 00 00 07 55 10 30 00 63 34 12 34 12 34 00 47 41 42 44 A4 7E";
    }

}
