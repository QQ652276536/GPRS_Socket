import com.zistone.message.MessageReceive;

public class Main
{
    public static void main(String[] args)
    {
        //模拟终端注册
        String str = "7E 01 00 00 2D 55 10 30 00 63 34 12 34 00 01 00 01 31 32 33 34 35 43 41 4E 42 4F 58 00 00 00 00" + " 00 00 00 00 00" +
                " 00 00 00 00 00 00 00 00 00 00 00 00 02 D4 C1 31 32 33 34 35 36 10 7E";
        MessageReceive messageReceive = new MessageReceive(str);


        System.out.println("_____________________成功启动_____________________");
    }
}
