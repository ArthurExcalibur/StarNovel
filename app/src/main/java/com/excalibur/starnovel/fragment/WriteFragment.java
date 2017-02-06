package com.excalibur.starnovel.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.excalibur.starnovel.R;
import com.excalibur.starnovel.adapter.WriteViewPagerAdapter;

/**
 * Created by Administrator on 2017/1/5.
 */
public class WriteFragment extends Fragment {

    private View rootView;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null){//只有第一次加载布局时才给viewPager设置adapter，否则会抛出异常java.lang.IllegalStateException: Recursive entry to executePendingTransactions
            rootView = inflater.inflate(R.layout.fra_write,container,false);
            tabLayout = (TabLayout) rootView.findViewById(R.id.fra_write_tabLayout);
            viewPager = (ViewPager) rootView.findViewById(R.id.fra_write_viewPager);
            WriteViewPagerAdapter adapter = new WriteViewPagerAdapter(getActivity().getSupportFragmentManager());
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

}
