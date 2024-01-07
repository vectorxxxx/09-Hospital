package xyz.funnyboy.yygh.common.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.FileInputStream;
import java.io.InputStream;

public class ImageBase64Util
{
    public static void main(String[] args) {
        String imageFile = "C:\\Users\\uxiah\\Pictures\\1.png";// 待处理的图片
        System.out.println(getImageString(imageFile));
    }

    public static String getImageString(String imageFile) {
        try (InputStream is = new FileInputStream(imageFile)) {
            byte[] data = new byte[is.available()];
            is.read(data);
            return new String(Base64.encodeBase64(data));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
