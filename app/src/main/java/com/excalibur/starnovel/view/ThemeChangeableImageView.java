package com.excalibur.starnovel.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.excalibur.starnovel.utils.ViewAttributeUtil;

/**
 * Created by Administrator on 2016/12/5.
 */
public class ThemeChangeableImageView extends ImageView implements ThemeChangeable {

    private int attr_src = -1;

    public ThemeChangeableImageView(Context context) {
        super(context);
    }

    public ThemeChangeableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        attr_src = ViewAttributeUtil.getSrcAttribute(attrs);
    }

    public ThemeChangeableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        attr_src = ViewAttributeUtil.getSrcAttribute(attrs);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setTmeme(Resources.Theme themeId) {
        if(attr_src != -1){
            ViewAttributeUtil.applySrc(this,themeId,attr_src);
        }
    }
}
