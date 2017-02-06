package com.excalibur.starnovel.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.excalibur.starnovel.fragment.WritePublicFragment;

/**
 * Created by Administrator on 2017/1/13.
 */
public class WriteViewPagerAdapter extends FragmentPagerAdapter {

    private String[] titles = new String[]{"微文","连载","私密"};

    public WriteViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new WritePublicFragment();
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
