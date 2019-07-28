package com.zistone.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * 字符转换工具类
 * 不支持特殊字符
 */
public class HexStrUtil
{
    //16进制数字字符集
    public static final String HEXSTRING = "0123456789ABCDEF";

    public static void main(String[] args)
    {
        System.out.println("____________________________________________________________________");
        //测试通过
        System.out.print("16进制的Str转16进制的byte[]:");
        PrintHexStr(HexStrToHexBytes("E69D8E"));
        System.out.println("byte[]转成16进制的Str:" + HexBytesToHexStr(new byte[]{(byte) 0xE6, (byte) 0x9D, (byte) 0x8E}));
        System.out.println("____________________________________________________________________");
        System.out.println("Unicode编码的中文转16进制的Str:" + DeUnicode("李小伟"));
        System.out.println("Unicode编码的中文转16进制的Str:" + DeUnicode("LiWei"));
        System.out.println("16进制的Str转成Unicode编码的中文:" + EnUnicode("674E4F1F"));
        System.out.println("16进制的Str转成Unicode编码的中文:" + EnUnicode("004C0069005700650069"));
        System.out.println("____________________________________________________________________");
    }

    /**
     * 分割16进制的Str,每两个字节分割,如:2B44EF->byte[]{0x2B, 0x44, 0xEF}
     *
     * @param hexStr
     * @return
     */
    public static byte[] HexStrSplitToBytes(String hexStr)
    {
        byte[] array = new byte[8];
        byte[] tmp = hexStr.getBytes();
        for (int i = 0; i < 4; i++)
        {
            array[i] = MergeASCII(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return array;
    }

    /**
     * 将两个ASCII字节合并为一个字符,如:0xEF->EF
     *
     * @param byte1
     * @param byte2
     * @return
     */
    public static byte MergeASCII(byte byte1, byte byte2)
    {
        byte temp1 = Byte.decode("0x" + new String(new byte[]{byte1})).byteValue();
        temp1 = (byte) (temp1 << 4);
        byte temp2 = Byte.decode("0x" + new String(new byte[]{byte2})).byteValue();
        byte resultByte = (byte) (temp1 ^ temp2);
        return resultByte;
    }

    /**
     * 16进制的Str转普通Str
     *
     * @param hexStr
     * @return
     */
    public static String HexStrToStr(String hexStr)
    {
        //能被16整除,肯定可以被2整除
        byte[] array = new byte[hexStr.length() / 2];
        try
        {
            for (int i = 0; i < array.length; i++)
            {
                array[i] = (byte) (0xff & Integer.parseInt(hexStr.substring(i * 2, i * 2 + 2), 16));
            }
            hexStr = new String(array, "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
        return hexStr;
    }

    /**
     * 普通Str转16进制Str
     *
     * @param str
     * @return
     */
    public static String StrToHexStr(String str)
    {
        //根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder stringBuilder = new StringBuilder(bytes.length * 2);
        //将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++)
        {
            stringBuilder.append("0x");
            stringBuilder.append(HEXSTRING.charAt((bytes[i] & 0xf0) >> 4));
            stringBuilder.append(HEXSTRING.charAt((bytes[i] & 0x0f) >> 0));
            //去掉末尾的逗号
            if (i != bytes.length - 1)
            {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 16进制的byte[]转16进制的Str
     *
     * @param array
     * @return
     */
    public static String HexBytesToHexStr(byte[] array)
    {
        if (null == array || array.length <= 0)
        {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < array.length; i++)
        {
            int temp = array[i] & 0xFF;
            String tempHStr = Integer.toHexString(temp);
            if (tempHStr.length() < 2)
            {
                stringBuilder.append(0);
            }
            stringBuilder.append(tempHStr);
        }
        return stringBuilder.toString();
    }

    /**
     * 16进制的Str转16进制的byte[]
     *
     * @param hexStr
     * @return
     */
    public static byte[] HexStrToHexBytes(String hexStr)
    {
        if (null == hexStr || hexStr.equals(""))
        {
            return null;
        }
        if (hexStr.contains("0x"))
        {
            hexStr = hexStr.replaceAll("0x", "");
        }
        hexStr = hexStr.toUpperCase();
        int length = hexStr.length() / 2;
        char[] hexChars = hexStr.toCharArray();
        byte[] array = new byte[length];
        for (int i = 0; i < length; i++)
        {
            int temp = i * 2;
            array[i] = (byte) (HEXSTRING.indexOf(hexStr.charAt(i)) << 4 | HEXSTRING.indexOf(hexChars[temp + 1]));
        }
        return array;
    }

    /**
     * 获取该字符对应的16进制
     *
     * @param str
     * @return
     */
    private static String GetHexStr(String str)
    {
        String hexStr = "";
        for (int i = str.length(); i < 4; i++)
        {
            if (i == str.length())
                hexStr = "0";
            else
                hexStr = hexStr + "0";
        }
        return hexStr + str;
    }

    /**
     * 16进制的Str转成Unicode编码的中文
     *
     * @param hexStr
     * @return
     */
    public static String EnUnicode(String hexStr)
    {
        String enUnicode = null;
        String deUnicode = null;
        for (int i = 0; i < hexStr.length(); i++)
        {
            if (enUnicode == null)
            {
                enUnicode = String.valueOf(hexStr.charAt(i));
            }
            else
            {
                enUnicode = enUnicode + hexStr.charAt(i);
            }
            if (i % 4 == 3)
            {
                if (enUnicode != null)
                {
                    if (deUnicode == null)
                    {
                        deUnicode = String.valueOf((char) Integer.valueOf(enUnicode, 16).intValue());
                    }
                    else
                    {
                        deUnicode = deUnicode + (char) Integer.valueOf(enUnicode, 16).intValue();
                    }
                }
                enUnicode = null;
            }
        }
        return deUnicode;
    }

    /**
     * Unicode编码的中文转16进制的Str
     *
     * @param str
     * @return
     */
    public static String DeUnicode(String str)
    {
        String hexStr = "0x";
        for (int i = 0; i < str.length(); i++)
        {
            if (i == 0)
            {
                hexStr = GetHexStr(Integer.toHexString(str.charAt(i)).toUpperCase());
            }
            else
            {
                hexStr = hexStr + GetHexStr(Integer.toHexString(str.charAt(i)).toUpperCase());
            }
        }
        return hexStr;
    }

    /**
     * 普通byte[]以16进制打印至控制台
     *
     * @param array
     */
    public static void PrintHexStr(byte[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            String hex = Integer.toHexString(array[i] & 0xFF);
            if (hex.length() == 1)
            {
                hex = '0' + hex;
            }
            System.out.print(hex.toUpperCase());
        }
        System.out.println();
    }

}
