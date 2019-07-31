package com.zistone.socket;

import com.zistone.message.MessageReceive;

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
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            String info;
            while ((info = bufferedReader.readLine()) != null)
            {
                System.out.println("收到来自客户端的信息:" + info);
            }
            //关闭客户端的输入流(不关闭服务端的输出流),此时m_socket连接并没关闭
            m_socket.shutdownInput();
            //解析客户端的信息
            if (null != info && !"".equals(info))
            {
                new MessageReceive(info);
            }
            //模拟业务处理
            //Thread.sleep(10000);

            outputStream = m_socket.getOutputStream();
            printWriter = new PrintWriter(outputStream);

            printWriter.write("~~~");
            printWriter.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //关闭资源
        finally
        {
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
