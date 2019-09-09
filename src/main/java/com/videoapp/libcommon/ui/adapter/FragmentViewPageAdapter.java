package com.videoapp.libcommon.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author - qqz
 * @date - 2019/6/12
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class FragmentViewPageAdapter extends FragmentPagerAdapter {

    /** Fragment列表 */
    private List<Fragment> mFragmentList;

    public FragmentViewPageAdapter(FragmentManager fm, @NonNull List<Fragment> fragments) {
        super(fm);
        mFragmentList = fragments;
    }

    /**
     * 设置数据并且刷新界面
     *
     * @param fragmentList Fragment列表
     */
    public void setData(List<Fragment> fragmentList) {
        this.mFragmentList = fragmentList;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    }
}
