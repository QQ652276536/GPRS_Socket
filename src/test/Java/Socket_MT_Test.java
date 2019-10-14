import com.zistone.socket.Socket_MT;
import org.junit.jupiter.api.Test;

public class Socket_MT_Test
{
    @Test
    public void SendData()
    {
        try
        {
            new Socket_MT().SendData("300234067349750◎REPORTINTERVAL◎STARTTIME◎1,1,1");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
