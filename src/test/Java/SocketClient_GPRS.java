
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class SocketClient_GPRS
{
    public static void main(String[] args)
    {
        try
        {
            Socket socket = new Socket("localhost", 8065);
            //得到一个输出流，用于向服务器发送数据
            OutputStream outputStream = socket.getOutputStream();
            System.out.println("请输入16进制数据:");
            Scanner sc = new Scanner(System.in);
            while (true)
            {
                String data = sc.nextLine();
                if ("exit".equals(data))
                {
                    return;
                }
                byte[] byteArray = HexStrToByteArray(data);
                outputStream.write(byteArray);
                //刷新缓冲
                outputStream.flush();
                //得到一个输入流，用于接收服务器响应的数据
                InputStream inputStream = socket.getInputStream();
                byte[] bytes = new byte[1]; // 一次读取一个byte
                String info = "";
                while (true)
                {
                    if (inputStream.available() > 0)
                    {
                        inputStream.read(bytes);
                        String hexStr = ByteArrayToHexStr(bytes);
                        info += HexStrToStr(hexStr);
                        //已经读完
                        if (inputStream.available() == 0)
                        {
                            System.out.println("收到来自服务端的信息:" + info);
                            break;
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 16进制Str转byte[]
     *
     * @param hexStr
     * @return
     */
    public static byte[] HexStrToByteArray(String hexStr)
    {
        if (hexStr == null)
        {
            return null;
        }
        if (hexStr.length() == 0)
        {
            return new byte[0];
        }
        byte[] byteArray = new byte[hexStr.length() / 2];
        for (int i = 0; i < byteArray.length; i++)
        {
            String subStr = hexStr.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte) Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }

    /**
     * byte[]转16进制Str
     *
     * @param byteArray
     */
    public static String ByteArrayToHexStr(byte[] byteArray)
    {
        if (byteArray == null)
        {
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int i = 0; i < byteArray.length; i++)
        {
            int temp = byteArray[i] & 0xFF;
            hexChars[i * 2] = hexArray[temp >>> 4];
            hexChars[i * 2 + 1] = hexArray[temp & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 16进制的Str转Str
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

}
