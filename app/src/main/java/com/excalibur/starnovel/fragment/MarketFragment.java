package com.excalibur.starnovel.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.excalibur.starnovel.R;

/**
 * Created by Excalibur on 2017/1/13.
 */
public class MarketFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("TestForFragment","MarketFragment onCreateView");
        return inflater.inflate(R.layout.fra_market,container,false);
    }
}
