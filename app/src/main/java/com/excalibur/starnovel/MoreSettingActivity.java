package com.excalibur.starnovel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.excalibur.starnovel.utils.Constants;

/**
 * Created by Excalibur on 2016/12/13.
 */
public class MoreSettingActivity extends BaseActivity implements View.OnClickListener{

    private SharedPreferences sp;

    private SwitchCompat volume_page;
    private SwitchCompat screen_page;

    private RadioGroup pageMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_setting);

        initDatas();
        initViews();
    }

    private void initDatas(){
        sp = getSharedPreferences(Constants.SHARE_PREFS,MODE_PRIVATE);
    }

    private void initViews(){
        volume_page = (SwitchCompat) findViewById(R.id.more_setting_volume);
        screen_page = (SwitchCompat) findViewById(R.id.more_setting_screen);
        setSwitchStatus(volume_page,0);
        setSwitchStatus(screen_page,1);

        int mode = sp.getInt(Constants.CHANGE_PAGE_MODE,0);
        pageMode = (RadioGroup) findViewById(R.id.more_setting_pageMode);
        RadioButton radioButton = (RadioButton) pageMode.getChildAt(mode);
        radioButton.setChecked(true);

        ImageButton back = (ImageButton) findViewById(R.id.more_setting_back);
        back.setOnClickListener(this);
    }

    private void setSwitchStatus(SwitchCompat compat,int index){
        boolean flag;
        if(index == 0){
            flag = sp.getBoolean(Constants.VOLUME_CHANGE_PAGE,true);
        }else if(index == 1){
            flag = sp.getBoolean(Constants.TOUCH_SCREEN_CHANGE_PAGE,true);
        }else if(index == 2){
            flag = sp.getBoolean(Constants.SHOW_NOTIFICATION,true);
        }else{
            flag = sp.getBoolean(Constants.LANDSCAPE,false);
        }
        compat.setChecked(flag);
    }

    private void saveSwitchStatus(){
        for (int i = 0; i < 2; i++) {
            if(i == 0){
                sp.edit().putBoolean(Constants.VOLUME_CHANGE_PAGE,volume_page.isChecked()).apply();
            }else{
                sp.edit().putBoolean(Constants.TOUCH_SCREEN_CHANGE_PAGE,screen_page.isChecked()).apply();
            }
        }
        int id = pageMode.getCheckedRadioButtonId();
        int index;
        if(id == R.id.more_setting_mode1){
            index = 0;
        }else if(id == R.id.more_setting_mode2){
            index = 1;
        }else{
            index = 2;
        }
        sp.edit().putInt(Constants.CHANGE_PAGE_MODE,index).apply();

        Intent intent = new Intent();
        intent.putExtra(Constants.CHANGE_PAGE_MODE,index);
        setResult(0x124,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        saveSwitchStatus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.more_setting_back:
                saveSwitchStatus();
                break;
        }
    }
}
