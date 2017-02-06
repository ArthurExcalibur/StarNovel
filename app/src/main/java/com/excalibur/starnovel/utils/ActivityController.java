package com.excalibur.starnovel.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/5.
 */
public class ActivityController {

    public static List<Activity> activityList = new ArrayList<>();

    public static void addActivity(Activity activity){
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }

    public static void finishAll(){
        for (int i = activityList.size() - 1; i >= 0;i--) {
            if(!activityList.get(i).isFinishing()) {
                activityList.get(i).finish();
            }
        }
    }

    public static Activity getCurrentActivity(){
        return activityList.get(activityList.size() - 1);
    }
}
