import com.zistone.socket.Server_GPRS;

import java.io.IOException;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            new Server_GPRS().start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
