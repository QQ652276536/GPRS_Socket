import com.zistone.socket.Server_GPRS;
import com.zistone.util.PropertiesUtil;
import org.apache.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;

public class Main
{
    private static int PORT_SOCKET;

    static
    {
        PORT_SOCKET = Integer.valueOf(PropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET1"));
    }

    private static Logger m_logger = Logger.getLogger(Main.class);

    public static void main(String[] args)
    {
        try
        {
            m_logger.debug(">>>GPRS的Socket服务启动,端口:" + PORT_SOCKET + ",等待终端连接...\r\n");
            ServerSocket server = new ServerSocket(PORT_SOCKET);
            int count = 0;
            while (true)
            {
                //开启监听
                Socket socket = server.accept();
                count++;
                m_logger.debug(">>>开启第" + count + "次长连接");
                Server_GPRS server_gprs_thread = new Server_GPRS(socket);
                server_gprs_thread.start();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
