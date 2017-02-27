package com.example.mafei.viewmanager.view.navigator.scrolltab.viewpager;

import android.os.Parcelable;
import android.view.View;

import com.example.mafei.viewmanager.view.navigator.scrolltab.viewpager.PagerAdapter;
import com.example.mafei.viewmanager.view.navigator.scrolltab.viewpager.ViewPager;

import java.util.ArrayList;

/**
 * Created by jicool on 2017/2/13.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private ArrayList<View> mViews;

    public ViewPagerAdapter(ArrayList<View> views) {
        mViews = views;
    }

    public void updateViews(ArrayList<View> views) {
        mViews = views;
    }

    @Override
    public int getCount() {
        return (null == mViews) ? 0 : mViews.size();
    }

    @Override
    public Object instantiateItem(View viewPager, int position) {
        ((ViewPager) viewPager).addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return mViews.indexOf(object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(View viewPager, int position, Object object) {
        ((ViewPager) viewPager).removeView(mViews.get(position));
    }

    @Override
    public void finishUpdate(View arg0) {

    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }

}
