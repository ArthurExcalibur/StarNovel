package com.excalibur.starnovel.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.excalibur.starnovel.ReadActivity;
import com.excalibur.starnovel.bean.Book;
import com.excalibur.starnovel.utils.ActivityController;

/**
 * Created by Excalibur on 2017/2/6.
 * save read percent when device shutdown during reading period.
 */
public class ShutDownReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Activity activity = ActivityController.getCurrentActivity();
        if(activity.getClass() == ReadActivity.class){
            Book book = ((ReadActivity)activity).book;
            book.setAddTime(0);
            book.save();
        }
    }

}
