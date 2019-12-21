package com.zistone.gprstest;

import com.zistone.gprstest.socket.Server_GPRS;
import com.zistone.gprstest.socket.Server_MO;
import com.zistone.gprstest.socket.Server_Set;

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

            //            server_mo = new Server_MO();
            //            server_mo.MyStart();

            server_setParam = new Server_Set(server_gprs, null);
            server_setParam.MyStart();

            //            //监听模拟工具生成的文本的方式来获取数据server_mo.MyStart();
            //            FileData fileData = new FileData();
            //            fileData.setM_path("C:\\demo\\sm_info.txt");
            //            fileData.setM_path("C:\\Users\\zistone\\Desktop\\gprs_info.txt");
            //            fileData.setM_time(5 * 1000 * 60);
            //            fileData.setM_encode("UTF-8");
            //            new FileContentEvent_YX(fileData);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        //挂起调用线程,被调用线程结束时才执行调用线程
        server_gprs.Join();
        //        server_mo.Join();
        server_setParam.Join();

        server_gprs.Stop();
        //        server_mo.Stop();
        server_setParam.Stop();
    }
}
