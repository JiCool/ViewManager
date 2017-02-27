package com.example.mafei.viewmanager.view.navigator.scrolltab;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;

import com.example.mafei.viewmanager.R;
import com.example.mafei.viewmanager.view.navigator.scrolltab.viewpager.ViewPagerAdapter;
import com.example.mafei.viewmanager.manager.interfaces.IViewManager;
import com.example.mafei.viewmanager.view.navigator.impl.TabHost;
import com.example.mafei.viewmanager.view.navigator.scrolltab.viewpager.ViewPager;
import com.example.mafei.viewmanager.view.navigator.views.TabSpec;
import com.example.mafei.viewmanager.view.navigator.impl.BaseTabHostContentView;
import com.example.mafei.viewmanager.view.navigator.interfaces.ITabHostContentView;
import com.example.mafei.viewmanager.manager.MyIntent;

import java.util.ArrayList;

/**
 * Created by jicool on 2017/2/27.
 */

public class ScrollTabHost extends TabHost implements ViewPager.OnPageChangeListener{
    private static final String SWITCH_SMOOTH_SCROLL = "SWITCH_SMOOTH_SCROLL";
    private ViewPager.OnPageChangeListener mListener;

    public ScrollTabHost(Context context, IViewManager viewManager, int style) {
        super(context, viewManager, style);
        setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void initContentView() {
        mContentView = new ViewPager(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mContentView.setLayoutParams(layoutParams);
        ((ViewPager) mContentView).setOnPageChangeListener(this);
    }

    @Override
    protected void initTitleView() {
        super.initTitleView();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        mTitleView.setLayoutParams(params);
        mTitleView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.setting_sub_tab_bg));
    }

    @Override
    public void addTabContentView(TabSpec tabSpec, Class<BaseTabHostContentView> cla) {
        super.addTabContentView(tabSpec, cla);
        updateAdapter();
    }

    @Override
    public void addTabContentView(int index, TabSpec tabSpec, Class<BaseTabHostContentView> cla) {
        super.addTabContentView(index, tabSpec, cla);
        updateAdapter();
    }

    private void updateAdapter() {
        ArrayList<View> childs = new ArrayList<View>();
        for (ITabHostContentView subView : mContentViews) {
            childs.add(subView.getView());
        }
        mContentView.removeAllViews();
        ViewPagerAdapter viewPageAdapter = (ViewPagerAdapter) ((ViewPager) mContentView).getAdapter();
        if (viewPageAdapter == null) {
            viewPageAdapter = new ViewPagerAdapter(childs);
            ((ViewPager) mContentView).setAdapter(viewPageAdapter);
        } else {
            viewPageAdapter.updateViews(childs);
        }
        viewPageAdapter.notifyDataSetChanged();
    }


    @Override
    public void switchToTabContentView(int index, MyIntent intent) {
        super.switchToTabContentView(index, intent);
        if (mContentView != null) {
            ITabHostContentView subView = mContentViews.get(index);
            boolean smoothScroll = true;
            if (intent != null) {
                smoothScroll = intent.getBooleanExtra(SWITCH_SMOOTH_SCROLL, true);
            }
            ((ViewPager) mContentView).setCurrentItem(index, smoothScroll);
            subView.onSelected(intent);
            if (mListener != null) {
                mListener.onPageSelected(index);
            }
        }
    }

    @Override
    public void removeAllTabContentView() {
        super.removeAllTabContentView();
        updateAdapter();
    }

    @Override
    public void removeTabContentView(int index) {
        super.removeTabContentView(index);
        updateAdapter();;
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {
        if (mListener != null) {
            mListener.onPageScrolled(paramInt1, paramFloat, paramInt2);
        }
    }

    @Override
    public void onPageSelected(int paramInt) {
        if (mCurrIndex != paramInt) {
            switchToTabContentView(paramInt);
        }
    }

    @Override
    public void onPageScrollStateChanged(int paramInt) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(paramInt);
        }
    }
}
