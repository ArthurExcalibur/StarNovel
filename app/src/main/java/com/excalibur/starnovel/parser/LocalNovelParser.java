package com.excalibur.starnovel.parser;


import android.text.TextUtils;
import com.excalibur.starnovel.bean.Book;
import com.excalibur.starnovel.bean.TitleInfo;
import com.excalibur.starnovel.utils.FileUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Excalibur on 2016/12/9.
 * @version 1.0
 *
 * This class will help you
 *
 */
public class LocalNovelParser{
    public String charset;
    private Book book;
    private List<TitleInfo> titleList;
    private boolean parseOver;
    private int currentIndex = -1;
    private RandomAccessFile randomAccessFile;

    public LocalNovelParser(Book book){
        this.book = book;
        titleList = new ArrayList<>();
        charset = FileUtils.getTextCharset(book.getPath());
        try {
            randomAccessFile = new RandomAccessFile(book.getPath(),"r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentContent(){
        if(currentIndex == 0){
            //try {
            //    if(titleList.get(1).getStartLength() == titleList.get(0).getTitle().getBytes(charset).length){
            //        currentIndex = 1;
            //    }
            //} catch (UnsupportedEncodingException e) {
            //    e.printStackTrace();
            //}
        }
        try {
            randomAccessFile.seek(titleList.get(currentIndex).getStartLength());
            long end;
            if(currentIndex < titleList.size() - 1){
                end = titleList.get(currentIndex + 1).getStartLength();
            }else{
                end = FileUtils.getFileTotalByteNumber(book.getPath());
            }
            end = end - titleList.get(currentIndex).getStartLength();
            byte[] b = new byte[(int) end];
            randomAccessFile.read(b);
            String content = new String(b,charset);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFormerContnet(){
        if(currentIndex == 0){
            return null;
        }else{
            currentIndex--;
            return getCurrentContent();
        }
    }

    public String getNextContent(){
        if(currentIndex < titleList.size() - 1){
            currentIndex++;
            return getCurrentContent();
        }
        return null;
    }

    public int getCurrentIndex(){
        return currentIndex;
    }

    public List<TitleInfo> getTitleList(){
        return titleList;
    }

    public boolean getParseOver(){
        return parseOver;
    }

    public TitleInfo getCurrentTitleInfo(){
        return titleList.get(currentIndex);
    }

    public String seekTo(int index){
        if(index < 0 || index > titleList.size() - 1){
            return null;
        }
        currentIndex = index;
        return getCurrentContent();
    }

    public String seekToByte(long number){
        int index = -1;
        for (int i = 0; i < titleList.size(); i++) {
            if(titleList.get(i).getStartLength() > number){
                index = i - 1;
                break;
            }else if(titleList.get(i).getStartLength() == number){
                index = i;
                break;
            }
        }
        if(index == -1){
            return null;
        }
        return seekTo(index);
    }

    //execute only once
    public void parseTitleInfo(){

        long time = System.currentTimeMillis();
        int count = 0;
        BufferedReader reader = null;
        InputStreamReader inputStreamReader = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(book.getPath());
            inputStreamReader = new InputStreamReader(inputStream,charset);
            reader = new BufferedReader(inputStreamReader);

            String line;
            int number = 5;

            TitleInfo titleInfo = new TitleInfo();
            titleInfo.setTitle(book.getName());
            titleInfo.setIndex(0);
            titleInfo.setStartLength(0);
            titleList.add(titleInfo);

            StringBuilder builder = new StringBuilder();
            int parseLength = 0;

            while ((line = reader.readLine()) != null){

                if(TextUtils.isEmpty(line)){
                    parseLength += 2;
                    continue;
                }
                if(line.trim().length() < 4){
                    if(number >= 5 && TitleMatches.isExtra(line)) {
                        count++;
                        parseLength += builder.toString().getBytes(charset).length;
                        builder.delete(0,builder.length());
                        titleInfo = new TitleInfo(count, line, parseLength);
                        titleList.add(titleInfo);
                        number = 0;
                        if (currentIndex == -1 && parseLength >= book.getPercent()) {
                            currentIndex = parseLength == book.getPercent() ? count : count - 1;
                        }

                    }
                }else{
                    if(number >= 5 && TitleMatches.isZhang(line)){
                        count++;
                        parseLength += builder.toString().getBytes(charset).length;
                        builder.delete(0,builder.length());
                        titleInfo = new TitleInfo(count,line, parseLength);
                        titleList.add(titleInfo);
                        number = 0;
                        if (currentIndex == -1 && parseLength >= book.getPercent()) {
                            currentIndex = parseLength == book.getPercent() ? count : count - 1;
                        }
                    }
                }

                builder.append(line);
                parseLength += 2;
                number++;
                if(number >= 200){
                    parseLength += builder.toString().getBytes(charset).length;
                    builder.delete(0,builder.length());
                    number = 5;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStreamReader != null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(currentIndex == -1){
                currentIndex = 0;
            }
            if(book.getPercent() == book.getByteNumber()){
                currentIndex = titleList.size() - 1;
            }
            parseOver = true;
            System.out.println("执行完毕，耗时 : " + (System.currentTimeMillis() - time) + ",检测到" + titleList.size() + "章");
        }
    }

    public void unbindFile(){
        if(randomAccessFile != null){
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
