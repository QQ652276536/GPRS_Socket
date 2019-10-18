package com.zistone;

import com.zistone.socket.Server_GPRS;
import com.zistone.socket.Server_MO;
import com.zistone.socket.Server_Set;

public class Main
{
    public static void main(String[] args)
    {
        Server_GPRS server_gprs;
        Server_MO server_mo;
        Server_Set server_setParam;
        try
        {
            server_gprs = new Server_GPRS();
            server_gprs.MyStart();
            server_mo = new Server_MO();
            server_mo.MyStart();
            server_setParam = new Server_Set(server_gprs, server_mo);
            server_setParam.MyStart();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        //挂起调用线程,被调用线程结束时才执行调用线程
        server_gprs.Join();
        server_mo.Join();
        server_setParam.Join();

        server_gprs.Stop();
        server_mo.Stop();
        server_setParam.Stop();
    }
}
