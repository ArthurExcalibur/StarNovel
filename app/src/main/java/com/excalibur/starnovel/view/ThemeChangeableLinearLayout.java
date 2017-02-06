package com.excalibur.starnovel.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.excalibur.starnovel.utils.ViewAttributeUtil;

/**
 * Created by Excalibur on 2016/12/12.
 */
public class ThemeChangeableLinearLayout extends LinearLayout implements ThemeChangeable {

    private int attr_background = -1;

    public ThemeChangeableLinearLayout(Context context) {
        super(context);
    }

    public ThemeChangeableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        attr_background = ViewAttributeUtil.getBackgroundAttribute(attrs);
    }

    public ThemeChangeableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        attr_background = ViewAttributeUtil.getBackgroundAttribute(attrs);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setTmeme(Resources.Theme themeId) {
        if(attr_background != -1){
            ViewAttributeUtil.applyBackGroundDrawable(this,themeId,attr_background);
        }
    }
}
