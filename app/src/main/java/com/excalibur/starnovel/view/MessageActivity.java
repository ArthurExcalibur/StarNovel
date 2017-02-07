package com.excalibur.starnovel.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.excalibur.starnovel.BaseActivity;
import com.excalibur.starnovel.R;

/**
 * Created by Excalibur on 2017/2/7.
 */
public class MessageActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_message_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_message_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_message_recycler);

    }
}
