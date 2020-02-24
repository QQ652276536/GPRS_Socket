package com.zistone.gprs.util;

public class MyASCIIUtil
{
    private static String ToHexUtil(int number)
    {
        String str = "";
        switch (number)
        {
            case 10:
                str += "A";
                break;
            case 11:
                str += "B";
                break;
            case 12:
                str += "C";
                break;
            case 13:
                str += "D";
                break;
            case 14:
                str += "E";
                break;
            case 15:
                str += "F";
                break;
            default:
                str += number;
                break;
        }
        return str;
    }

    public static String ToHex(int number)
    {
        StringBuilder stringBuilder = new StringBuilder();
        if (number / 16 == 0)
        {
            return ToHexUtil(number);
        }
        else
        {
            String str = ToHex(number / 16);
            int temp = number % 16;
            stringBuilder.append(str).append(ToHexUtil(temp));
        }
        return stringBuilder.toString();
    }

    public static String ParseASCII(String str)
    {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] array = str.getBytes();
        for (int i = 0; i < array.length; i++)
            stringBuilder.append(ToHex(array[i]));
        return stringBuilder.toString();
    }

}
