import com.zistone.gprs.util.MyConvertUtil;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class SocketClient_MO
{
    public static void main(String[] args)
    {
        try
        {
            //Socket socket = new Socket("129.204.165.206", 5000);
            Socket socket = new Socket("localhost", 5000);
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
                byte[] byteArray = MyConvertUtil.HexStrToByteArray(data);
                outputStream.write(byteArray);
                //刷新缓冲
                outputStream.flush();
                //得到一个输入流，用于接收服务器响应的数据
                InputStream inputStream = socket.getInputStream();
                //一次读取一个byte
                byte[] bytes = new byte[1];
                String info = "";
                while (inputStream.available() > 0)
                {
                    inputStream.read(bytes);
                    String hexStr = MyConvertUtil.ByteArrayToHexStr(bytes);
                    info += MyConvertUtil.HexStrToStr(hexStr);
                    if (inputStream.available() == 0)
                    {
                        System.out.println("收到来自服务端的信息:" + info);
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
