package com.excalibur.starnovel.utils;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import com.excalibur.starnovel.view.ThemeChangeable;

/**
 * Created by Excalibur on 2016/12/5.
 */
public class ThemeUtils {

    public static void changeTheme(View rootView, Resources.Theme theme){

        if(rootView instanceof ThemeChangeable){
            ((ThemeChangeable)rootView).setTmeme(theme);
            if(rootView instanceof ViewGroup){
                int count = ((ViewGroup)rootView).getChildCount();
                for (int i = 0;i < count ; i++){
                    changeTheme(((ViewGroup)rootView).getChildAt(i),theme);
                }
            }
        }

    }
}
