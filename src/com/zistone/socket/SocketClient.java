package com.zistone.socket;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class SocketClient
{
    public static void main(String[] args)
    {
        try
        {
            Socket socket = new Socket("localhost", 8888);
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
                byte[] byteArray = hexStringToByteArray(data);
                outputStream.write(byteArray);
                //刷新缓冲
                outputStream.flush();


                //获取服务器端的响应数据
                //得到一个输入流，用于接收服务器响应的数据
                InputStream inputStream = socket.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                DataInputStream dataInputStream = new DataInputStream(bis);
                byte[] bytes = new byte[1]; // 一次读取一个byte
                String info = "";
                while (true)
                {
                    if (dataInputStream.available() > 0)
                    {
                        dataInputStream.read(bytes);
                        String tempStr = new String(bytes);
                        info += tempStr;
                        //已经读完
                        if (dataInputStream.available() == 0)
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
     * 16进制表示的字符串转换为字节数组
     *
     * @param hexString 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String hexString)
    {
        hexString = hexString.replaceAll(" ", "");
        int len = hexString.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
        {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return bytes;
    }

}
