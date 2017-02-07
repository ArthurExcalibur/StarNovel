package com.excalibur.starnovel.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.excalibur.starnovel.utils.ViewAttributeUtil;

/**
 * Created by Excalibur on 2017/2/7.
 */
public class ThemeChangeableRecyclerView extends RecyclerView implements ThemeChangeable {

    private int attr_background = -1;

    public ThemeChangeableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        attr_background = ViewAttributeUtil.getBackgroundAttribute(attrs);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setTmeme(Resources.Theme themeId) {
        if(-1 != attr_background){
            ViewAttributeUtil.applyBackGroundDrawable(this,themeId,attr_background);
        }
    }
}
