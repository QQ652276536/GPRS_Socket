package com.zistone;

import com.zistone.socket.Server_GPRS;
import com.zistone.socket.Server_MO;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

public class Main
{
    private static int PORT_SOCKET;
    private static int PORT_SOCKET2;

    static
    {
        PORT_SOCKET = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET1"));
        PORT_SOCKET2 = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET2"));
    }

    private static Logger m_logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception
    {
        Server_GPRS server_gprs;
        try {
            server_gprs = new Server_GPRS(PORT_SOCKET);
            server_gprs.start();
            m_logger.debug(">>>GPRS的Socket服务启动成功,端口:" + PORT_SOCKET);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Server_MO server_mo;
        try {
            server_mo = new Server_MO(PORT_SOCKET2);
            server_mo.start();
            m_logger.debug(">>>MO方式接收数据的Socket服务启动,端口:" + PORT_SOCKET2);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        server_gprs.join();
        server_mo.join();

        server_gprs.stop();
        server_mo.stop();
    }
}
