package com.zistone.message;

import java.util.Arrays;

/**
 * 终端注册
 */
public class ClientResPonse
{
    public ClientResPonse(String[] hexStrArray)
    {
        RecevieHexStrArray(hexStrArray);
    }

    public ClientResPonse(String hexStr)
    {
        RecevieHexStr(hexStr);
    }

    private String RecevieHexStrArray(String[] hexStrArray)
    {
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
        return "-1";
    }

    private String RecevieHexStr(String hexStr)
    {
        return "-1";
    }
    
}
