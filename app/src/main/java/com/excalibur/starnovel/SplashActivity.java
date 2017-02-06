package com.excalibur.starnovel;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.excalibur.starnovel.receiver.ShutDownReceiver;

/**
 * Created by Administrator on 2017/1/5.
 */
public class SplashActivity extends BaseActivity {

    ShutDownReceiver receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new ShutDownReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SHUTDOWN);
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != receiver){
            unregisterReceiver(receiver);
        }
    }
}
