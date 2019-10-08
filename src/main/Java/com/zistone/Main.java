package com.zistone;

import com.zistone.socket.Server_GPRS;
import com.zistone.socket.Server_MO;


public class Main
{
    public static void main(String[] args) throws Exception
    {
        Server_GPRS server_gprs;
        Server_MO server_mo;
        try
        {
            server_gprs = new Server_GPRS();
            server_gprs.MyStart();
            server_mo = new Server_MO();
            server_mo.MyStart();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //如果开启服务时发生异常则直接中断
            return;
        }

        //挂起调用线程,被调用线程结束时才执行调用线程
        server_gprs.Join();
        server_mo.Join();

        server_gprs.Stop();
        server_mo.Stop();
    }
}
