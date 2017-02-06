package com.excalibur.starnovel.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.View;

import com.excalibur.starnovel.utils.ViewAttributeUtil;

/**
 * Created by Excalibur on 2016/12/10.
 */
public class ThemeChangeableDrawerLayout extends DrawerLayout implements ThemeChangeable{

    private int attr_backColor = -1;

    public ThemeChangeableDrawerLayout(Context context) {
        super(context);
    }

    public ThemeChangeableDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        attr_backColor = ViewAttributeUtil.getBackgroundAttribute(attrs);
    }

    public ThemeChangeableDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        attr_backColor = ViewAttributeUtil.getBackgroundAttribute(attrs);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setTmeme(Resources.Theme themeId) {
        if(attr_backColor != -1){
            ViewAttributeUtil.applyBackGroundDrawable(this,themeId,attr_backColor);
        }
    }
}
