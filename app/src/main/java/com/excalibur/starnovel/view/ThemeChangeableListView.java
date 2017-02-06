package com.excalibur.starnovel.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.excalibur.starnovel.utils.ViewAttributeUtil;

/**
 * Created by Administrator on 2017/1/11.
 */
public class ThemeChangeableListView extends ListView implements ThemeChangeable {

    private int attr_backgroundColor = -1;

    public ThemeChangeableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        attr_backgroundColor = ViewAttributeUtil.getBackgroundAttribute(attrs);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setTmeme(Resources.Theme themeId) {
        if(attr_backgroundColor != -1){
            ViewAttributeUtil.applyBackGroundDrawable(this,themeId,attr_backgroundColor);
        }
    }
}
