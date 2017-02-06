package com.excalibur.starnovel.view;

import android.content.res.Resources;
import android.view.View;

/**
 * Created by Excalibur on 2016/12/2.
 */
public interface ThemeChangeable {
    View getView();
    void setTmeme(Resources.Theme themeId);
}
