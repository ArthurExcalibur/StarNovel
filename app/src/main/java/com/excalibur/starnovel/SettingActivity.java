package com.excalibur.starnovel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Excalibur on 2016/11/17.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initViews();
    }

    private void initViews(){
        ImageButton button = (ImageButton) findViewById(R.id.activity_setting_back);
        button.setOnClickListener(this);
        ImageView user = (ImageView) findViewById(R.id.activity_setting_user);
        //user.setOnClickListener(this);
        View account = findViewById(R.id.activity_setting_account);
        account.setOnClickListener(this);
        View about = findViewById(R.id.activity_setting_about);
        View update = findViewById(R.id.activity_setting_update);
        View advice = findViewById(R.id.activity_setting_advice);
        about.setOnClickListener(this);
        update.setOnClickListener(this);
        advice.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_setting_back:
                finish();
                break;
            case R.id.activity_setting_user:
                break;
            case R.id.activity_setting_about:
                changeActivity(AboutActivity.class,null);
                break;
            case R.id.activity_setting_update:
                Toast.makeText(this,"当前已经是最新版本",Toast.LENGTH_SHORT).show();
                break;
            case R.id.activity_setting_advice:
                break;
        }
    }

    public void changeActivity(Class<? extends Activity> activity,String extra){
        Intent intent = new Intent(this,activity);
        if(extra != null){

        }
        startActivity(intent);
    }
}
