package com.zistone.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;

/**
 * 字符转换工具类
 * 不支持特殊字符
 */
public class ConvertUtil
{
    //16进制数字字符集
    public static final String HEXSTRING = "0123456789ABCDEF";

    public static void main(String[] args)
    {
        System.out.println("普通Str转16进制Str:" + StrToHexStr("0"));

        System.out.println("____________________________________________________________________");
        //测试通过
        System.out.println("普通Str转16进制Str:" + StrToHexStr("ErrorID"));
        System.out.println("16进制Str转普通Str:" + HexStrToStr("23406021597019"));
        System.out.println("byte[]转成16进制的Str:" + HexBytesToHexStr(new byte[]{(byte) 0xE6, (byte) 0x9D, (byte) 0x8E}));
        System.out.println("____________________________________________________________________");
        System.out.println("Unicode编码的中文转16进制的Str:" + DeUnicode("李小伟"));
        System.out.println("Unicode编码的中文转16进制的Str:" + DeUnicode("LiWei"));
        System.out.println("16进制的Str转成Unicode编码的中文:" + EnUnicode("674E5C0F4F1F"));
        System.out.println("16进制的Str转成Unicode编码的中文:" + EnUnicode("004C0069005700650069"));
        System.out.println("____________________________________________________________________");
    }

    /**
     * 10进制的Str转BCD
     *
     * @param str
     * @return
     */
    public static byte[] StrToBCD(String str)
    {
        int len = str.length();
        int mod = len % 2;
        if (mod != 0)
        {
            str = "0" + str;
            len = str.length();
        }
        byte byteArray1[];
        if (len >= 2)
        {
            len = len / 2;
        }
        byte byteArray2[] = new byte[len];
        byteArray1 = str.getBytes();
        int j, k;
        for (int p = 0; p < str.length() / 2; p++)
        {
            if ((byteArray1[2 * p] >= '0') && (byteArray1[2 * p] <= '9'))
            {
                j = byteArray1[2 * p] - '0';
            }
            else if ((byteArray1[2 * p] >= 'a') && (byteArray1[2 * p] <= 'z'))
            {
                j = byteArray1[2 * p] - 'a' + 0x0a;
            }
            else
            {
                j = byteArray1[2 * p] - 'A' + 0x0a;
            }
            if ((byteArray1[2 * p + 1] >= '0') && (byteArray1[2 * p + 1] <= '9'))
            {
                k = byteArray1[2 * p + 1] - '0';
            }
            else if ((byteArray1[2 * p + 1] >= 'a') && (byteArray1[2 * p + 1] <= 'z'))
            {
                k = byteArray1[2 * p + 1] - 'a' + 0x0a;
            }
            else
            {
                k = byteArray1[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            byteArray2[p] = b;
        }
        return byteArray2;
    }

    /**
     * BCD转10进制的Str
     *
     * @param array
     * @return
     */
    public static String BCDTo10Str(byte[] array)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < array.length; i++)
        {
            stringBuilder.append((byte) ((array[i] & 0xf0) >>> 4));
            stringBuilder.append((byte) (array[i] & 0x0f));
        }
        if (stringBuilder.toString().substring(0, 1).equalsIgnoreCase("0"))
        {
            return stringBuilder.toString().substring(1);
        }
        else
        {
            return stringBuilder.toString();
        }
    }

    /**
     * 将byte转换为一个长度为8的byte数组,数组每个值代表bit
     *
     * @param b
     * @return
     */
    public static byte[] ByteTo8ByteArray(byte b)
    {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--)
        {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    /**
     * 把byte转为字符串的bit
     *
     * @param b
     * @return
     */
    public static String ByteToBitStr(byte b)
    {
        return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1) + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1) + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1) + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    /**
     * 二进制字符串转byte
     *
     * @param binaryStr
     * @return
     */
    public static byte BinaryStrToByte(String binaryStr)
    {
        int re, len;
        if (null == binaryStr)
        {
            return 0;
        }
        len = binaryStr.length();
        if (len != 4 && len != 8)
        {
            return 0;
        }
        //8bit处理
        if (len == 8)
        {
            //正数
            if (binaryStr.charAt(0) == '0')
            {
                re = Integer.parseInt(binaryStr, 2);
            }
            //负数
            else
            {
                re = Integer.parseInt(binaryStr, 2) - 256;
            }
        }
        //4bit处理
        else
        {
            re = Integer.parseInt(binaryStr, 2);
        }
        return (byte) re;
    }

    /**
     * short转byte[]
     *
     * @param number
     * @return 两位字节的数组
     */
    public static byte[] ShortToBytes(short number)
    {
        int temp = number;
        byte[] array = new byte[2];
        for (int i = 0; i < array.length; i++)
        {
            //将最低位保存在最低位
            array[i] = new Integer(temp & 0xff).byteValue();
            //向右移8位
            temp = temp >> 8;
        }
        return array;
    }

    /**
     * int转byte[]
     *
     * @param num
     * @return 四位的字节数组
     */
    public static byte[] IntToBytes(int num)
    {
        byte[] b = new byte[4];
        b[0] = (byte) (0xff & num);
        b[1] = (byte) ((0xff00 & num) >> 8);
        b[2] = (byte) ((0xff0000 & num) >> 16);
        b[3] = (byte) ((0xff000000 & num) >> 24);
        return b;
    }

    /**
     * byte[]转int
     *
     * @param array 两位字节的数组
     * @return
     */
    public static short BytesToShort(byte[] array)
    {
        short s;
        //最低位
        short s0 = (short) (array[0] & 0xff);
        short s1 = (short) (array[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    /**
     * byte[]转int
     *
     * @param array 四位的字节数组
     * @return
     */
    public static int BytesToInt(byte[] array)
    {
        int num = array[0] & 0xFF;
        num |= ((array[1] << 8) & 0xFF00);
        num |= ((array[2] << 16) & 0xFF0000);
        num |= ((array[3] << 24) & 0xFF000000);
        return num;
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
            array[i] = MergehexStrII(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return array;
    }

    /**
     * 将两个hexStrII字节合并为一个字符,如:0xEF->EF
     *
     * @param byte1
     * @param byte2
     * @return
     */
    public static byte MergehexStrII(byte byte1, byte byte2)
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
     * 普通byte[]转16进制Str
     *
     * @param array
     */
    public static String ByteArrayToHexStr(byte[] array)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < array.length; i++)
        {
            String hex = Integer.toHexString(array[i] & 0xFF);
            if (hex.length() == 1)
            {
                stringBuilder.append("0");
            }
            stringBuilder.append(hex);
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

}
