package com.excalibur.starnovel.application;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Excalibur on 2016/11/18.
 *
 * Step1:define your crashHandler implements Thread.UncaughtExceptionHandler
 * Step2:complete function uncaughtException(Thread thread, Throwable ex) to storage crashInfo to local storage(sdcard or fileDir)
 * Step3:define an broadcastReceiver to listen NetworkState change and send info to Server when net is available(Wifi is better)
 * Step4:delete file that has been sent to Server
 *
 * Note:Do not forget to init your crashHandler in your application
 *
 */
public class NovelCrashHandler implements Thread.UncaughtExceptionHandler {

    private static NovelCrashHandler handler = new NovelCrashHandler();

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;

    private NovelCrashHandler(){

    }

    public void init(Context context){
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    public static NovelCrashHandler getInstance(){
        return handler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        if(mDefaultHandler != null && ! storageLogToLocal(thread,ex)){
            mDefaultHandler.uncaughtException(thread,ex);
        }else{
            //使用Toast来显示异常信息
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }.start();

            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

    }

    private boolean storageLogToLocal(Thread thread, Throwable ex){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return false;
        }

        File f = new File(NovelApplication.FILE_PATH);
        if(!f.exists()){
            f.mkdirs();
        }

        long time = System.currentTimeMillis();
        String t = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date(time));

        File file = new File(NovelApplication.FILE_PATH + "crash" + t + ".ifs");
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(file);
            writer.println("crashTime : " + t);

            storagePhoneInfo(writer);

            ex.printStackTrace(writer);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }finally {
            if(writer != null){
                writer.close();
            }
        }

        return true;
    }

    private void storagePhoneInfo(PrintWriter writer){
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);

            writer.println("APP Version : " + info.versionCode + "_" + info.versionName);
            writer.println("OS Version : " + Build.VERSION.RELEASE + "_" + Build.VERSION.SDK_INT);
            writer.println("Model : " + Build.MODEL);//型号
            writer.println("Vender : " + Build.MANUFACTURER);//制造商
            writer.println("CPU_ARM : " + Build.CPU_ABI);//CPU架构

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
