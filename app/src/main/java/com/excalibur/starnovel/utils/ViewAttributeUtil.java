package com.excalibur.starnovel.utils;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.excalibur.starnovel.view.ThemeChangeable;

/**
 * Created by Excalibur on 2016/12/2.
 */
public class ViewAttributeUtil {

    public static int getAttributeValue(AttributeSet attr, int paramInt){
        int value = -1;
        int count = attr.getAttributeCount();
        for (int i = 0; i < count; i++) {
            if(attr.getAttributeNameResource(i) == paramInt){
                String str = attr.getAttributeValue(i);
                if(null != str && str.startsWith("?")){
                    value = Integer.valueOf(str.substring(1,str.length())).intValue();
                    return value;
                }
            }
        }
        return value;
    }

    public static int getBackgroundAttribute(AttributeSet attr){
        return getAttributeValue(attr,android.R.attr.background);
    }

    public static int getTextColorAttribute(AttributeSet attr){
        return getAttributeValue(attr,android.R.attr.textColor);
    }

    public static int getSrcAttribute(AttributeSet attr){
        return getAttributeValue(attr,android.R.attr.src);
    }

    public static void applyBackGroundDrawable(ThemeChangeable able, Resources.Theme theme,int paramInt){
        TypedArray a = theme.obtainStyledAttributes(new int[]{paramInt});
        Drawable drawable = a.getDrawable(0);
        if(null != able){
            (able.getView()).setBackgroundDrawable(drawable);
        }
        a.recycle();
    }

    public static void applyTextColor(ThemeChangeable able, Resources.Theme theme,int paramInt){
        TypedArray a = theme.obtainStyledAttributes(new int[]{paramInt});
        int resourceId = a.getColor(0,0);
        if(null != able && able instanceof TextView){
            ((TextView)able.getView()).setTextColor(resourceId);
        }
        a.recycle();
    }

    public static void applySrc(ThemeChangeable able, Resources.Theme theme,int paramsInt){
        TypedArray a = theme.obtainStyledAttributes(new int[]{paramsInt});
        int resourceId = a.getResourceId(0,0);
        if(null != able && able instanceof ImageView){
            ((ImageView)able).setImageResource(resourceId);
        }
        a.recycle();
    }
}
