package com.excalibur.starnovel.application;

import android.content.Context;

import com.excalibur.starnovel.bean.Book;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/5.
 */
public class NovelApplication extends org.litepal.LitePalApplication {

    private static Context mContext;
    public static String FILE_PATH;

    public static List<Book> allBoosInSQLite;

    public static boolean isManageMode;
    public static boolean isEditMode;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        FILE_PATH = mContext.getFilesDir() + "/crash/log/";
        allBoosInSQLite = DataSupport.order("lastReadTime").find(Book.class);
        //NovelCrashHandler handler = NovelCrashHandler.getInstance();
        //handler.init(mContext);
    }

    public static void removeToFirst(Book book){
        if(allBoosInSQLite.isEmpty()){
            return;
        }
        List<Book> list = new ArrayList<>();
        for (Book b : allBoosInSQLite) {
            if(!book.getPath().equals(b.getPath())){
                list.add(b);
            }
        }
        list.add(book);
        allBoosInSQLite = list;
        //list.clear();
    }

    public static Book getFirstBook(){
        for (int i = allBoosInSQLite.size() - 1;i >= 0;i--){
            if(allBoosInSQLite.get(i).getAddTime() != -1){
                return allBoosInSQLite.get(i);
            }
        }
        return null;
    }

    public static Context getContext(){
        return mContext;
    }

}

