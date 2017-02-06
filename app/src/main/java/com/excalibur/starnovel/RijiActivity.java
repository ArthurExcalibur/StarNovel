package com.excalibur.starnovel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/1/13.
 */
public class RijiActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riji);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_riji_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.activity_riji_coll);
        collapsingToolbarLayout.setTitle("AAAAAAAA");

        ImageView imageView = (ImageView) findViewById(R.id.activity_riji_image);
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.activity_riji_floating);

        if(getIntent().getStringExtra("path") == null){//表示没有图片
            imageView.setBackgroundResource(R.mipmap.ic_launcher);
        }else{
            imageView.setBackgroundResource(R.mipmap.background);
        }

        TextView textView = (TextView) findViewById(R.id.activity_riji_text);
        textView.setText(getContent());
    }

    private String getContent(){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            builder.append("TestForRijiTestTest");
        }
        return builder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_riji,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_riji_share){
            Toast.makeText(this,"分享",Toast.LENGTH_SHORT).show();
            return true;
        }else if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
