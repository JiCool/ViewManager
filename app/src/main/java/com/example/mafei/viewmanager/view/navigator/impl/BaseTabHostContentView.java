package com.example.mafei.viewmanager.view.navigator.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.animationlibrary.library.Animators;
import com.example.mafei.viewmanager.manager.interfaces.IViewManager;
import com.example.mafei.viewmanager.view.navigator.interfaces.ITabHostContentView;
import com.example.mafei.viewmanager.manager.MyIntent;
import com.example.mafei.viewmanager.manager.ViewLaunchMode;

/**
 * Created by jicool on 2017/2/21.
 */

public class BaseTabHostContentView implements ITabHostContentView {
    protected IViewManager mViewManager;
    protected Context mContext;
    protected boolean mSelected;
    protected LinearLayout mContentView;
    MyIntent mIntent = null;

    protected void switchToView(MyIntent intent) {
        mViewManager.startView(intent);
    }

    @Override
    public boolean isSelected() {
        return mSelected;
    }

    @Override
    public void onSelected(MyIntent intent) {
        if(!mSelected)
        {
            createView(intent);
        }
        mSelected = true;
    }

    @Override
    public void onUnSelected() {
        mSelected = false;
        mIntent = null;
    }

    @Override
    public View getView() {
        createView(null);
        return mContentView;
    }

    @Override
    public void createView(MyIntent intent) {
        if(intent != null)
        {
            mIntent = intent;
        }
    }

    @Override
    public void resumeView() {
//        createView(mIntent);
        if(mContentView != null) {
            mContentView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideView() {
        if(mContentView != null)
        {
            mContentView.setVisibility(View.GONE);
        }
    }

    @Override
    public void destroyView() {
        if(mContentView != null)
        {
            mContentView.removeAllViews();
            mContentView = null;
        }
        mIntent = null;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public void setIntent(MyIntent intent) {
        mIntent = intent;
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

    private int mRequestCode;

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


    public BaseTabHostContentView(Context context, IViewManager viewManager) {

        mContext = context;
        mViewManager = viewManager;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

    }
}
