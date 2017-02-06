package com.excalibur.starnovel.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.excalibur.starnovel.utils.ViewAttributeUtil;

/**
 * Created by Excalibur on 2016/12/2.
 */
public class ThemeChangeableRelativeLayout extends RelativeLayout implements ThemeChangeable {

    private int backgroundColor = -1;

    public ThemeChangeableRelativeLayout(Context context) {
        super(context);
    }

    public ThemeChangeableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        backgroundColor = ViewAttributeUtil.getBackgroundAttribute(attrs);
    }

    public ThemeChangeableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        backgroundColor = ViewAttributeUtil.getBackgroundAttribute(attrs);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setTmeme(Resources.Theme themeId) {
        if(backgroundColor != -1){
            ViewAttributeUtil.applyBackGroundDrawable(this,themeId,backgroundColor);
        }
    }
}
