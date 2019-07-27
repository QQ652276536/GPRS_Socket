package com.zistone.util;

public class GB2312Util
{
    public static void main(String[] args) throws Exception
    {
        byte[] bs = "李伟".getBytes("GB2312");
        String str = "";
        for (int i = 0; i < bs.length; i++)
        {
            int temp = Integer.parseInt(BytesToHexStr(new byte[]{bs[i]}), 16);
            str += (temp - 0x80 - 0x20) + "";
        }
        System.out.println(str);
    }

    public static String BytesToHexStr(byte[] array)
    {
        String str = "";
        for (int i = 0; i < array.length; i++)
        {
            String hexStr = Integer.toHexString(array[i] & 0xFF);
            if (hexStr.length() == 1)
            {
                hexStr = '0' + hexStr;
            }
            str += hexStr.toUpperCase();
        }
        return str;
    }

}
