package com.zistone.socket;

import com.zistone.message.MessageReceive;
import com.zistone.util.ConvertUtil;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread
{
    private Socket m_socket;

    public ServerThread(Socket socket)
    {
        this.m_socket = socket;
    }

    @Override
    public void run()
    {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        OutputStream outputStream = null;
        PrintWriter printWriter = null;
        try
        {
            inputStream = m_socket.getInputStream();
            String info = "";
            /***************************如果终端发送的是字符串使用下面这段代码***************************/
            //            inputStreamReader = new InputStreamReader(inputStream);
            //            bufferedReader = new BufferedReader(inputStreamReader);
            //            while ((info = bufferedReader.readLine()) != null)
            //            {
            //                System.out.println("收到来自终端的信息:" + info);
            //            }
            /*************************如果终端发送的是16进制数据使用下面这段代码*************************/
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
            //一次读取一个byte
            byte[] bytes = new byte[1];
            while (dataInputStream.read(bytes) != -1)
            {
                info += ConvertUtil.ByteArrayToHexStr(bytes) + " ";
                //返回下一个方法调用可以不受阻塞地从此流读取或跳过的估计字节数
                if (dataInputStream.available() == 0)
                {
                }
            }
            System.out.println(">>>收到来自终端的信息:" + info);

            //关闭终端的输入流(不关闭服务端的输出流),此时m_socket连接并没关闭
            m_socket.shutdownInput();
            //解析终端的信息
            String responseStr = "Null...";
            if (null != info && !"".equals(info))
            {
                responseStr = new MessageReceive().RecevieHexStr(info);
            }
            //模拟业务处理Thread.sleep(10000);
            outputStream = m_socket.getOutputStream();
            printWriter = new PrintWriter(outputStream);
            printWriter.write(responseStr);
            printWriter.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //关闭资源
        finally
        {
            System.out.println(">>>终端已断开本次连接");
            try
            {
                if (printWriter != null)
                    printWriter.close();
                if (outputStream != null)
                    outputStream.close();
                if (bufferedReader != null)
                    bufferedReader.close();
                if (inputStreamReader != null)
                    inputStreamReader.close();
                if (inputStream != null)
                    inputStream.close();
                if (m_socket != null)
                    m_socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

}
