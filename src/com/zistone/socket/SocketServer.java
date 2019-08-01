package com.zistone.socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer
{
    public static void main(String[] args)
    {
        //        new SocketServer();
        new SocketServer("");
    }

    private static volatile boolean RECEIVESENDFLAG = false;
    private Socket m_socket;
    private boolean m_receiveThreadFlag = false;
    private boolean m_sendThreadFlag = false;

    public SocketServer()
    {
        try
        {
            System.out.println("_____________________服务启动_____________________");
            ServerSocket server = new ServerSocket(8888);
            while (true)
            {
                //开启监听
                m_socket = server.accept();
                //线程运行过程中不要启动新的线程,避免IllegalThreadStateException
                if (!m_receiveThreadFlag)
                {
                    System.out.println("_____________________接收终端的线程启动");
                    ReceiveThread.start();
                    m_receiveThreadFlag = true;
                }
                if (!m_sendThreadFlag)
                {
                    System.out.println("_____________________响应终端的线程启动");
                    SendThread.start();
                    m_sendThreadFlag = true;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public SocketServer(String content)
    {
        try
        {
            System.out.println(">>>服务启动,等待终端的连接\n");
            ServerSocket server = new ServerSocket(8888);
            int count = 0;
            while (true)
            {
                //开启监听
                Socket socket = server.accept();
                count++;
                System.out.println(">>>第" + count + "个终端连接成功");
                ServerThread thread = new ServerThread(socket);
                thread.start();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 接收终端内容的线程
     */
    Thread ReceiveThread = new Thread(() ->
    {
        while (!RECEIVESENDFLAG)
        {
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            try
            {
                inputStream = m_socket.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);

                String info;
                while ((info = bufferedReader.readLine()) != null)
                {
                    System.out.println("收到来自终端的信息:" + info);
                    //确认收到终端的信息后再唤醒响应终端的线程
                    RECEIVESENDFLAG = true;
                }
                //关闭终端的输入流(不关闭服务端的输出流),此时Socket连接并没关闭
                m_socket.shutdownInput();
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
                    if (bufferedReader != null)
                        bufferedReader.close();
                    if (inputStreamReader != null)
                        inputStreamReader.close();
                    if (inputStream != null)
                        inputStream.close();
                    //Socket关闭由响应终端的线程来关闭
                    //                    if (m_socket != null)
                    //                        m_socket.close();
                }
                //发生异常时需要唤醒响应线程,达到通知终端服务有异常
                catch (IOException e)
                {
                    RECEIVESENDFLAG = true;
                    e.printStackTrace();
                }
            }
        }
    });

    /**
     * 响应终端的线程
     *
     * @param socket
     */
    Thread SendThread = new Thread(() ->
    {
        while (RECEIVESENDFLAG)
        {
            OutputStream outputStream = null;
            PrintWriter printWriter = null;
            try
            {
                outputStream = m_socket.getOutputStream();
                printWriter = new PrintWriter(outputStream);
                printWriter.write("<---stackoverflow");
                printWriter.flush();
                RECEIVESENDFLAG = true;
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
                    if (m_socket != null)
                        m_socket.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    });

}
