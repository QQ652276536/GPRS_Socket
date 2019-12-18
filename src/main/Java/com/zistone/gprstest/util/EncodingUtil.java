package com.zistone.gprstest.util;

import java.io.UnsupportedEncodingException;

public class EncodingUtil
{
    public static void main(String[] args)
    {
        System.out.println("GBK转Unicode:" + GBKToUnicode("B2E2CAD4"));
        System.out.println("Unicode转GBK:" + UnicodeToGBK("测试"));
    }

    /**
     * GBK转Unicode
     *
     * @param str
     * @return
     */
    public static String GBKToUnicode(String str)
    {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0, j = 0; i < str.length(); i += 2, j++)
        {
            bytes[j] = Integer.decode("0x" + str.substring(i, i + 2)).byteValue();
        }
        try
        {
            return new String(bytes, "GBK");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return str;
        }
    }

    /**
     * Unicode转GBK
     *
     * @param str
     * @return
     */
    public static String UnicodeToGBK(String str)
    {
        byte[] tmp;
        String result = "";
        try
        {
            tmp = str.getBytes("GBK");
            for (int i = 0; i < tmp.length; i++)
            {
                int value = tmp[i] + 256;
                result += "0x" + Integer.toHexString(value) + ",";
            }
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return result.toUpperCase();
    }

}
