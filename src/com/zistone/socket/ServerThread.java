package com.zistone.socket;

import com.sun.security.ntlm.Server;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread
{
    private Socket socket;
    private String content;

    public ServerThread(Socket socket, String content)
    {
        this.socket = socket;
        this.content = content;
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
            inputStream = socket.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            String info;
            while ((info = bufferedReader.readLine()) != null)
            {
                System.out.println("收到来自客户端的信息:" + info);
            }
            //关闭客户端的输入流(不关闭服务端的输出流),此时Socket连接并没关闭
            socket.shutdownInput();

            outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(outputStream);
            Thread.sleep(10000);
            printWriter.write(content);

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
                if (socket != null)
                    socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

}
