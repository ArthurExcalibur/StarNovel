package com.excalibur.starnovel.utils;

import android.content.Context;
import android.util.DisplayMetrics;


/**
 * Created by Excalibur on 2016/11/24.
 */
public class ScreenUtils {

    public static DisplayMetrics getScreenMetrics(Context context){
        return context.getResources().getDisplayMetrics();
    }

    public static int convertDip2Px(Context context,float dpValue){
        float desnity = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * desnity + 0.5f);
    }

    public static int convertPx2Dip(Context context,float pxValue){
        float desnity = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / desnity + 0.5f);
    }

    public static int convertSp2Px(Context context,float spValue){
        float scaleDesnity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(spValue * scaleDesnity + 0.5f);
    }

    public static int convertPx2Sp(Context context,float pxValue){
        float scaleDesnity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(pxValue / scaleDesnity + 0.5f);
    }

}
