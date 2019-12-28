import com.zistone.gprs.util.ConvertUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileSize
{
    public static void main(String[] args) throws IOException
    {
        FileInputStream fileInputStream = new FileInputStream(new File("C:\\Users\\zistone\\Desktop\\微信图片_20191128155529.jpg"));
        byte[] data = new byte[fileInputStream.available()];
        fileInputStream.read(data);
        int sum = 0;
        String hexStr = ConvertUtil.ByteArrayToHexStr(data);
        double length = hexStr.length() / 2;
        for (int i = 0; i < length; i++)
        {
            String subStr = hexStr.substring(2 * i, 2 * i + 2);
            int num = Integer.parseInt(subStr, 16);
            System.out.println("16进制:" + subStr + "\t10进制:" + num);
            sum += num;
        }
        //        BASE64Encoder encoder = new BASE64Encoder();
        //        String base64Str = encoder.encode(data);
        System.out.println(sum);
    }

    /**
     * @功能 精确计算base64字符串文件大小（单位：B）
     * @注意 base64字符串(不含data : audio / wav ; base64, 文件头)
     */
    public static double base64file_size(String base64String)
    {

        //1.获取base64字符串长度(不含data:audio/wav;base64,文件头)
        int size0 = base64String.length();

        //2.获取字符串的尾巴的最后10个字符，用于判断尾巴是否有等号，正常生成的base64文件'等号'不会超过4个
        String tail = base64String.substring(size0 - 10);

        //3.找到等号，把等号也去掉,(等号其实是空的意思,不能算在文件大小里面)
        int equalIndex = tail.indexOf("=");
        if (equalIndex > 0)
        {
            size0 = size0 - (10 - equalIndex);
        }

        //4.计算后得到的文件流大小，单位为字节
        return size0 - (int) (size0 / 8) * 2;
    }

}
