package com.excalibur.starnovel.utils;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

import com.excalibur.starnovel.view.ThemeChangeableTextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Excalibur on 2016/11/21.
 */
public class FileUtils {

    public static List<Map<String,String>> fileLists = new ArrayList<>();

    /**
     * 根据所给的路径和文件后缀查找所有该后缀的文件
     * @param dirFile 查询路径
     * @param pix 所查询的文件后缀名
     * @return 所有符合要求的文件路径集合
     */
    public List<Map<String,String>> getFileListFromLocal(File dirFile,String pix){

        File[] listFile = dirFile.listFiles();

        if(listFile != null){
            for (int i = 0;i < listFile.length;i++){
                File numberFile = listFile[i];
                if(numberFile.isDirectory()){
                    fileLists.addAll(getFileListFromLocal(numberFile,pix));
                }else{
                    if(numberFile.getName().endsWith(pix)){
                        Map<String,String> map = new HashMap<>();
                        map.put("title",numberFile.getAbsolutePath().substring(numberFile.getAbsolutePath().lastIndexOf("\\")));
                        map.put("comment",String.valueOf(numberFile.getTotalSpace()));
                        fileLists.add(map);
                    }
                }
            }
        }

        return fileLists;
    }

    /**
     * 根据文件路径判断文件存储字符格式
     * @return 文件存储字符格式
     */
    public static String getTextCharset(String filePath){
        byte[] first3bytes = new byte[3];
        FileInputStream is = null;
        try {
            is = new FileInputStream(filePath);
            is.read(first3bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String charset;
        if(first3bytes[0] == (byte)0xEF && first3bytes[1] == (byte)0xBB && first3bytes[2] == (byte)0xBF){
            charset = "UTF-8";
        }else if(first3bytes[0] == (byte)0xFF && first3bytes[1] == (byte)0xFE){
            charset = "unicode";
        }else if(first3bytes[0] == (byte)0xFE && first3bytes[1] == (byte)0xFF){
            charset = "UTF-16BE";
        }else if(first3bytes[0] == (byte)0xFF && first3bytes[1] == (byte)0xFF){
            charset = "UTF-16LE";
        }else{
            charset = "GBK";
        }

        return charset;
    }

    public static String getBookPercent(long percent,long totalNumber){
        double per = percent * 1.0 / totalNumber * 100;
        if(per < 1){
            return "0" + Constants.DATA_FORMAT.format(per);
        }else if(per <= 100){
            return Constants.DATA_FORMAT.format(per);
        }else{
            return "100";
        }
    }

    /**
     * 根据文件获取该文件大小，单位为B,K,M,G
     * @param file 要获取其大小的文件
     * @return
     */
    public static String getFileSize(File file){
        DecimalFormat df = new DecimalFormat("#.00");
        String result;
        long byteNumber = getFileTotalByteNumber(file.getAbsolutePath());
        if(byteNumber < 1024){
            result = df.format((double)byteNumber) + "B";
        }else if(byteNumber < 1024 * 1024){
            result = df.format((double)byteNumber / 1024) + "K";
        }
        else if(byteNumber < 1024 * 1024 * 1024){
            result = df.format((double)byteNumber / (1024 * 1024)) + "M";
        }
        else{
            result = df.format((double)byteNumber / (1024 * 1024 * 1024)) + "G";
        }
        return result;
    }

    /**
     * 根据文件路径返回该文件所占的字符块数目
     * @param path 文件路径
     * @return 所占字符块数目
     */
    public static long getFileTotalByteNumber(String path){
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
            return inputStream.available();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    /**
     * 根据单章的内容将其自动分页，返回分页后的字符集合
     * @param text 单章内容
     * @param paint 显示文本的TextView的画笔(用来传递文字属性)
     * @param width 显示文本的TextView的宽度
     * @param maxCellCount 显示文本的TextView所能容纳的最大列数
     * @return 分页后的字符集合
     */
    public static List<String> autoSplitText(String text,Paint paint,int width,int maxCellCount){
        int count = 0;
        List<String> list = new ArrayList<>();
        String[] newTextLines = text.replaceAll("\r","").split("\n");
        StringBuilder builder = new StringBuilder();
        for (String value : newTextLines){
            if(!value.equals("")){
                if(paint.measureText(value) <= width){
                    builder.append(value);
                }else{
                    float lineWidth = 0;
                    for(int cnt = 0;cnt != value.length();cnt++){
                        char ch = value.charAt(cnt);
                        lineWidth += paint.measureText(String.valueOf(ch));
                        if(lineWidth <= width){
                            builder.append(ch);
                        }else{
                            lineWidth = 0;
                            --cnt;
                            builder.append("\n");
                            count++;
                            if(count == maxCellCount){
                                count = 0;
                                list.add(builder.toString());
                                builder = new StringBuilder();
                            }
                        }
                    }
                }
                builder.append("\n").append("\n");
                count+=2;
            }
            if(count >= maxCellCount){
                count = 0;
                list.add(builder.toString());
                builder = new StringBuilder();
            }
        }

        //if(!text.endsWith("\n") && builder!= null){
        //    builder.deleteCharAt(builder.length() - 1);
        //}

        String left = builder.toString();
        if(left != null && !TextUtils.isEmpty(left)){
            list.add(left);
        }
        return list;
    }

    public static int getMaxAvaliableTextNumber(ThemeChangeableTextView textView,int pos,float titleHeight){
        int viewHeight = textView.getHeight();
        int firstH = getLineHeight(0,textView);
        int otherH = getLineHeight(1,textView);
        if(pos == 0){
            viewHeight -= titleHeight;
        }
        return (viewHeight - firstH) / otherH + 1;
    }

    public static int getLineHeight(int line,ThemeChangeableTextView textView){
        Rect rect = new Rect();
        textView.getLineBounds(line,rect);
        return rect.bottom - rect.top;
    }

    public static boolean isEmpty(String str){
        if (str == null || str.trim().length() == 0)
            return true;
        else
            return false;
    }

}
