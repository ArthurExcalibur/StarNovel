package com.excalibur.starnovel.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.excalibur.starnovel.utils.ViewAttributeUtil;

/**
 * Created by Administrator on 2017/1/11.
 */
public class ThemeChangeableScrollView extends ScrollView implements ThemeChangeable {

    private int backgroundColor = -1;
    private View titleView;

    public ThemeChangeableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        backgroundColor = ViewAttributeUtil.getBackgroundAttribute(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup rootView = (ViewGroup) getChildAt(0);
        titleView = rootView.getChildAt(0);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
