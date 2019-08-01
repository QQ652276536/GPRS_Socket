package com.zistone.socket;

import com.zistone.message.MessageReceive;
import com.zistone.util.ConvertUtil;

import java.io.*;
import java.net.Socket;

/**
 * 短连接
 */
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
        //字节输入流
        InputStream inputStream = null;
        //字节输出流
        OutputStream outputStream = null;
        //字节输入流到字符输入流的转换
        InputStreamReader inputStreamReader = null;
        //加快字符读取速度
        BufferedReader bufferedReader = null;
        //缓冲输入流
        BufferedInputStream bufferedInputStream = null;
        //数据输入流
        DataInputStream dataInputStream = null;
        //
        PrintWriter printWriter = null;
        try
        {
            inputStream = m_socket.getInputStream();
            String info = "";
            /***************************如果终端发送的是字符串使用下面这段代码***************************/
            //            inputStreamReader = new InputStreamReader(inputStream);
            //            bufferedReader = new BufferedReader(inputStreamReader);
            //            //注意,readLine()方法如果没有读到报文结束符(换行)会一直阻塞
            //            while ((info = bufferedReader.readLine()) != null)
            //            {
            //                System.out.println(">>>线程" + this.getId() + "收到来自终端的信息:" + info);
            //            }
            /*************************如果终端发送的是16进制数据使用下面这段代码*************************/
            bufferedInputStream = new BufferedInputStream(inputStream);
            dataInputStream = new DataInputStream(bufferedInputStream);
            //一次读取一个byte
            byte[] bytes = new byte[1];
            //注意,read()方法如果没有数据会一直阻塞,也就是永远不会等于-1,除非客户端调用close(),如果想在while循环外部获取数据则需要设定跳出条件
            while ((dataInputStream.read(bytes)) != -1)
            {
                String tempStr = ConvertUtil.ByteArrayToHexStr(bytes) + " ";
                info += tempStr;
                //返回下次调用可以不受阻塞地从此流读取或跳过的估计字节数,如果等于0则表示已经读完
                if (dataInputStream.available() == 0)
                {
                    System.out.println(">>>终端信息读取完毕,最后一位:" + tempStr);
                    break;
                }
            }
            System.out.println(">>>线程" + this.getId() + "收到来自终端的信息:" + info);
            //关闭终端的输入流(不关闭服务端的输出流),此时m_socket虽然没有关闭,但是客户端已经不能再发送消息
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
            System.out.println(">>>本次连接已断开\n");
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
