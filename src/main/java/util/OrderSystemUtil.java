package util;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class OrderSystemUtil {
    // 需要实现读取 body 的功能.
    // 需要先把整个 body 读取出来, 然后才能去解析 JSON.
    public static String readBody(HttpServletRequest request) throws UnsupportedEncodingException {
        int length = request.getContentLength();
        byte[] bytes = new byte[length];
        try ( InputStream inputStream = request.getInputStream()){
           inputStream.read(bytes,0,length);
        } catch (IOException e) {
            e.printStackTrace();
        }//保持格式统一
        return new String(bytes,"UTF-8");
    }
}
