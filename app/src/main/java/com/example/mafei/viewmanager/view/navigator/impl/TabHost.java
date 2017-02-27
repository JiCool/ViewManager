package com.example.mafei.viewmanager.view.navigator.impl;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.animationlibrary.library.Animators;
import com.example.mafei.viewmanager.R;
import com.example.mafei.viewmanager.manager.interfaces.IViewManager;
import com.example.mafei.viewmanager.view.navigator.views.TabSpec;
import com.example.mafei.viewmanager.view.navigator.interfaces.ITabHost;
import com.example.mafei.viewmanager.view.navigator.interfaces.ITabHostContentView;
import com.example.mafei.viewmanager.manager.MyIntent;
import com.example.mafei.viewmanager.manager.ViewLaunchMode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Created by jicool on 2017/2/24.
 */

public class TabHost extends LinearLayout implements ITabHost, View.OnClickListener {
    protected Context mContext;
    public static final int STYLE_TITLE_TOP = 1;

    public static final int STYLE_TITLE_BOTTOM = 2;
    protected LinearLayout mTitleView;
    protected ViewGroup mContentView;
    View divider = null;
    protected int mCurrIndex = -1;
    protected MyIntent mIntent;
    /**
     * 所有的子view的titleview集合
     */
    protected ArrayList<TabSpec> mTitleViews;

    /**
     * 所有的子view集合
     */
    protected ArrayList<BaseTabHostContentView> mContentViews;

    IViewManager mViewManager;

    public TabHost(Context context, IViewManager viewManager, int style) {
        super(context);
        mContext = context;
        mViewManager = viewManager;
        setOrientation(VERTICAL);
        initTitleView();
        initContentView();
        if (style == STYLE_TITLE_TOP) {
            addView(mTitleView);
            addView(mContentView);
        } else {
            addView(mContentView);
            addView(mTitleView);
        }
    }

    protected void initContentView() {
        mContentView = new LinearLayout(getContext());
        LayoutParams contentLayoutParams = new LayoutParams(LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentLayoutParams.weight = 1.0f;
        mContentView.setLayoutParams(contentLayoutParams);
        mContentView.setBackgroundColor(getContext().getResources().getColor(R.color.tab_background_color));
    }

    public void setDivider(View divider) {
        this.divider = divider;
    }

    protected void initTitleView() {
        mTitleView = new LinearLayout(getContext());
//        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
//                (int) mContext.getResources().getDimension(R.dimen.common_tab_bottom_title_height));
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mTitleView.setLayoutParams(params);
        mTitleView.setOrientation(LinearLayout.HORIZONTAL);
        mTitleView.setBackgroundResource(R.drawable.setting_tab_bottom_bg);
        // addView(mTitleView);
    }


    public ArrayList<TabSpec> getTabWidget() {
        return mTitleViews;
    }

    public TabSpec newTabSpec() {
        return new TabSpec(mContext);
    }

    @Override
    public void onClick(View view) {
        int index = mTitleViews.indexOf(view);
        if (index != mCurrIndex) {
            switchToTabContentView(index);
        }
    }

    @Override
    public void addTabContentView(TabSpec tabSpec, Class<BaseTabHostContentView> cla) {
        addTabContentView(-1, tabSpec, cla);
    }

    @Override
    public void addTabContentView(int index, TabSpec tabSpec, Class<BaseTabHostContentView> cla) {
        Constructor cls = null;
        try {
            cls = cla
                    .getDeclaredConstructor(new
                            Class[]{Context
                            .class,
                            IViewManager.class});
            cls.setAccessible(true);
            addTabContent(index, (BaseTabHostContentView) cls
                    .newInstance(mContext, mViewManager));
            addTitle(index, tabSpec);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void addTabContent(int index, BaseTabHostContentView tabContentView) {
        if (mContentViews == null) {
            mContentViews = new ArrayList<>();
        }
        if (index <= -1) {
            mContentViews.add(tabContentView);
            mContentView.addView(tabContentView.getView());
        } else {
            mContentViews.add(index, tabContentView);
            mContentView.addView(tabContentView.getView(), index);
        }
    }

    private void addTitle(int index, TabSpec tabSpec) {
        if (mTitleViews == null) {
            mTitleViews = new ArrayList<>();
        }
        tabSpec.setOnClickListener(this);
        int viewIndex = -1;
        int titleIndex = -1;
        int dividerIndex = -1;
        if (mTitleViews.size() == 0) {
            // 此时index必为0
            titleIndex = 0;
            viewIndex = 0;
        } else {
            // 需要考虑分割符
            if (divider != null) {
                // 有分隔符
                if (index != -1) {
                    titleIndex = index;
                    viewIndex = index * 2;
                    dividerIndex = index * 2 + 1;
                } else {
                    titleIndex = mTitleViews.size();
                    dividerIndex = (mTitleViews.size() - 1) * 2 + 1;
                    viewIndex = (mTitleViews.size() - 1) * 2 + 2;
                }

            } else {
                // 没有分隔符
                if (index != -1) {
                    titleIndex = index;
                    viewIndex = index;
                } else {
                    titleIndex = mTitleViews.size();
                    viewIndex = mTitleViews.size();
                }
            }
        }

        if (titleIndex != -1) {
            mTitleViews.add(titleIndex, tabSpec);
        }

        if (dividerIndex != -1 && divider != null) {
            mTitleView.addView(divider, dividerIndex);
        }

        if (viewIndex != -1) {
            mTitleView.addView(tabSpec, viewIndex);
        }

    }

    @Override
    public void removeTabContentView(BaseTabHostContentView tabContentView) {
        if (mContentViews.contains(tabContentView)) {
            int index = mContentViews.indexOf(tabContentView);
            removeTabContentView(index);
        }

    }

    @Override
    public void removeAllTabContentView() {

        mContentView.removeAllViews();
        mTitleView.removeAllViews();
        mTitleViews.clear();
        mContentViews.clear();
    }

    @Override
    public void removeTabContentView(int index) {
        if (index < 0) {
            return;
        }
        if (mTitleViews.size() == mContentViews.size()) {
            mTitleViews.remove(index);
        } else {
            throw new RuntimeException("mTitleViews.size() != mContentViews.size()");
        }
        mContentViews.remove(index);
    }

    @Override
    public void switchToTabContentView(int index) {
        switchToTabContentView(index, null);
    }

    @Override
    public void switchToTabContentView(int index, MyIntent intent) {
        if (!checkIndexValid(index)) {
            return;
        }

        if (mCurrIndex != -1) {
            TabSpec title = mTitleViews.get(mCurrIndex);
            title.setState(false);
        }

        // 上个子view非选中
        if (mCurrIndex != -1) {
            BaseTabHostContentView lastSubView = mContentViews.get(mCurrIndex);
            lastSubView.onUnSelected();
        }

        mCurrIndex = index;
        // 切换到下个子view
        switchToTitle(index);
        switchToContent(index, intent);
    }


    private void switchToContent(int index, MyIntent intent) {
        // View content = subView.getView();
        int size = mContentView.getChildCount();

        for (int i = 0; i < size; i++) {
            View view = mContentView.getChildAt(i);
            if (i == index) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }

        BaseTabHostContentView subView = mContentViews.get(index);
        subView.onSelected(intent);
    }

    private void switchToTitle(int index) {
        TabSpec title = mTitleViews.get(index);
        title.setState(true);
    }

    protected boolean checkIndexValid(int index) {
        if (mContentViews == null) {
            return false;
        }

        if (index < 0 || index >= mContentViews.size()) {
            return false;
        }


        return true;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void createView(MyIntent intent) {
        mIntent = intent;
    }

    @Override
    public void resumeView() {
        if (isContentViewEmpty()) {
            return;
        }
        setVisibility(View.VISIBLE);
        if (mCurrIndex == -1) {
            mCurrIndex = 0;
        }
        ITabHostContentView subView = mContentViews.get(mCurrIndex);
        subView.resumeView();
    }

    protected boolean isContentViewEmpty() {
        if (mContentView == null || mContentViews.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public void hideView() {
        if (isContentViewEmpty()) {
            return;
        }
        setVisibility(View.INVISIBLE);
        if (mCurrIndex != -1) {
            ITabHostContentView subView = mContentViews.get(mCurrIndex);
            subView.hideView();
        }
    }

    @Override
    public void destroyView() {
        if (isContentViewEmpty()) {
            return;
        }
        for (ITabHostContentView subTabView : mContentViews) {
            mContentView.removeView(subTabView.getView());
            subTabView.destroyView();
        }
        for (TabSpec tabSpec : mTitleViews) {
            mTitleView.removeView(tabSpec);
            tabSpec.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (isContentViewEmpty()) {
            return;
        }

        if (mCurrIndex != -1) {
            ITabHostContentView subView = mContentViews.get(mCurrIndex);
            subView.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void setIntent(MyIntent intent) {
        mIntent = intent;
        mContentViews.get(mCurrIndex).setIntent(intent);
    }

    @Override
    public ViewLaunchMode getLaunchMode() {
        return ViewLaunchMode.Standard;
    }

    private int mViewFlag = 0;

    @Override
    public int getViewFlag() {
        return mViewFlag;
    }

    @Override
    public void setViewFlag(int flag) {
        mViewFlag = flag;
    }

    @Override
    public void setResult(int resultCode, MyIntent intent) {

    }


    @Override
    public void onViewResult(int requestCode, int resultCode, MyIntent intent) {

    }

    protected int mRequestCode;

    @Override
    public void setRequestCode(int requestCode) {
        mRequestCode = requestCode;
    }

    @Override
    public int getRequestCode() {
        return mRequestCode;
    }

    @Override
    public Animators getEnterAnimator() {
        return null;
    }

    @Override
    public Animators getExitAnimator() {
        return null;
    }

    public boolean isEmpty()
    {
        if(mContentViews == null)
        {
            return true;
        }
        return mContentViews.isEmpty();
    };
}
