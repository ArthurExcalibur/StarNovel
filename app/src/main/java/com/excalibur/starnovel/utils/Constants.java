package com.excalibur.starnovel.utils;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2017/1/5.
 */
public class Constants {
    public static final String SHARE_PREFS = "config";

    public static final String IS_NIGHTMODE = "nightMode";
    public static final String THMEE = "theme";

    public static final DecimalFormat DATA_FORMAT = new DecimalFormat("#.00");

    //用于配置阅读界面的配置信息
    public static final String TEXTSIZE = "textSize";//字号
    public static final String THEMEID = "theme";//主题
    public static final String LINESPACE = "lineSpace";//行间距
    public static final String BRIGHTNESS = "brightness";//亮度

    public static final String LAST_SCROLL_Y = "last_scroll_y";


    //用于在更多设置界面设置属性的标签
    public static final String IS_FIRST_USE = "isFirstUse";//第一次阅读时展示功能
    public static final String VOLUME_CHANGE_PAGE = "volume_change_page";//音量键翻页
    public static final String TOUCH_SCREEN_CHANGE_PAGE = "screen_change_page";//触摸屏幕两侧翻页
    public static final String SHOW_NOTIFICATION = "show_notification";//显示通知栏
    public static final String LANDSCAPE = "landscape";//横屏
    public static final String CHANGE_PAGE_MODE = "change_page_mode";//页面切换模式
    public static final String SCREEN_LIGHT_TIME = "screen_light_time";//屏保时间
}
