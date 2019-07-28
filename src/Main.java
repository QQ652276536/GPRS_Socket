import com.zistone.util.HexStrUtil;

import java.util.Arrays;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("_____________________服务启动_____________________");
        //模拟终端注册
        String str = "7E 01 00 00 2D 55 10 30 00 63 34 12 34 00 01 00 01 31 32 33 34 35 43 41 4E 42 4F 58 00 00 00 00" +
                " 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 D4 C1 31 32 33 34 35 36 10 7E";
        //str = str.replaceAll(" ","");
        String[] strArray = str.split(" ");
        //前后两个标识位
        String flag1 = strArray[0];
        String flag2 = strArray[strArray.length - 1];
        //去掉标识位
        String[] strArray2 = new String[strArray.length - 2];
        System.arraycopy(strArray, 1, strArray2, 0, strArray2.length);
        //消息ID
        String[] id = Arrays.copyOfRange(strArray2, 0, 2);
        //TODO:根据消息ID判断消息类型
        //消息体属性
        String[] body = Arrays.copyOfRange(strArray2, 2, 4);
        //终端手机号
        String[] phone = Arrays.copyOfRange(strArray2, 4, 10);
        //消息流水号
        String[] detail = Arrays.copyOfRange(strArray2, 10, 12);
        //消息包封装项
        String[] packageItem = Arrays.copyOfRange(strArray2, 12, strArray2.length);


        System.out.println("_____________________成功启动_____________________");
    }
}
