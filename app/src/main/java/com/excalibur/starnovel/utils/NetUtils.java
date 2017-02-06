package com.excalibur.starnovel.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Excalibur on 2016/12/20.
 */
public class NetUtils {

    public static String getStringFromNetInputStream(InputStream is){
        String result = null;

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int len;
        byte[] b = new byte[1024];

        try {
            while((len = is.read(b)) != -1){
                out.write(b,0,len);
            }
            result = new String(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(null != out){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}
