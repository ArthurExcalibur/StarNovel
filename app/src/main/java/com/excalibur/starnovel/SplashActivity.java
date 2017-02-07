package com.excalibur.starnovel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.excalibur.starnovel.utils.Constants;

/**
 * Created by Administrator on 2017/1/5.
 */
public class SplashActivity extends BaseActivity {

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkForUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void checkForUpdate(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            int code = info.versionCode;
            //从网上获取到最新版的versionCode
            int newCode = 10;
            if(code != newCode){
                //说明有版本更新
                SharedPreferences sp = getSharedPreferences(Constants.SHARE_PREFS,MODE_PRIVATE);
                int notCode = sp.getInt(Constants.NOT_UPDATE_CODE,-1);
                if(notCode != -1 && newCode > notCode){
                    //弹出弹框提示更新
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }finally {

        }
    }
}
