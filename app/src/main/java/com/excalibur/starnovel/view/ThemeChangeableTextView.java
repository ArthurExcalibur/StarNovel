package com.excalibur.starnovel.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.excalibur.starnovel.utils.ViewAttributeUtil;

/**
 * Created by Excalibur on 2016/12/2.
 */
public class ThemeChangeableTextView extends TextView implements ThemeChangeable {

    private int attr_textColor = -1;
    private int attr_bgDrawable = -1;

    public ThemeChangeableTextView(Context context) {
        super(context);
    }

    public ThemeChangeableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        attr_textColor = ViewAttributeUtil.getTextColorAttribute(attrs);
        attr_bgDrawable = ViewAttributeUtil.getBackgroundAttribute(attrs);
    }

    public ThemeChangeableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        attr_bgDrawable = ViewAttributeUtil.getBackgroundAttribute(attrs);
        attr_textColor = ViewAttributeUtil.getTextColorAttribute(attrs);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setTmeme(Resources.Theme themeId) {
        if(attr_textColor != -1){
            ViewAttributeUtil.applyTextColor(this,themeId,attr_textColor);
        }
        if(attr_bgDrawable != -1){
            ViewAttributeUtil.applyBackGroundDrawable(this,themeId,attr_bgDrawable);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //if(MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY
        //        && MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY
        //        && getWidth()> 0
        //        && getHeight() > 0
        //        && mEnabled){
        //    String newText = autoSplitText(this);
        //    setText(newText);
        //}
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }

}
