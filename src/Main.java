import com.zistone.socket.Server_GPRS;
import com.zistone.socket.Server_MO;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;

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

    public static void main(String[] args)
    {
        try
        {
            ServerSocket server = new ServerSocket(PORT_SOCKET);
            ServerSocket server2 = new ServerSocket(PORT_SOCKET2);
            while (true)
            {
                //开启监听
                Socket socket = server.accept();
                Server_GPRS server_gprs_thread = new Server_GPRS(socket);
                server_gprs_thread.start();
                m_logger.debug(">>>GPRS的Socket服务启动,端口:" + PORT_SOCKET + ",等待终端连接...\r\n");

                Socket socket2 = server2.accept();
                Server_MO server_mo_thread = new Server_MO(socket2);
                server_mo_thread.start();
                m_logger.debug(">>>MO方式接收数据的Socket服务启动,端口:" + PORT_SOCKET2 + ",等待终端连接...\r\n");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
