import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

public class WebSocket_Client_Test
{
    private static String uri = "ws://localhost:8080/Websocket";
    private static Session session;

    private void start()
    {
        WebSocketContainer container = null;
        try
        {
            container = ContainerProvider.getWebSocketContainer();
        }
        catch (Exception ex)
        {
            System.out.println("error" + ex);
        }
        try
        {
            URI r = URI.create(uri);
            session = container.connectToServer(WebSocket_Client.class, r);
        }
        catch (DeploymentException | IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        WebSocket_Client_Test client = new WebSocket_Client_Test();
        client.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        try
        {
            do
            {
                input = br.readLine();
                if (!input.equals("exit"))
                    client.session.getBasicRemote().sendText("javaclient" + input);
            }
            while (!input.equals("exit"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
