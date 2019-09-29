package com.zistone;

import com.zistone.socket.Server_GPRS;
import com.zistone.socket.Server_MO;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

public class Main
{
    private static int PORT_SOCKET_GPRS;
    private static int PORT_SOCKET_MO;

    static
    {
        PORT_SOCKET_GPRS = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET1"));
        PORT_SOCKET_MO = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET2"));
    }

    private static Logger m_logger = Logger.getLogger(Main.class);

    public static void main(String[] args)
    {
        try
        {
            Server_GPRS server_gprs_thread = new Server_GPRS(PORT_SOCKET_GPRS);
            server_gprs_thread.start();

            Server_MO server_mo_thread = new Server_MO(PORT_SOCKET_MO);
            server_mo_thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
