package com.example.mafei.viewmanager.view.impl;

import android.content.Context;
import android.view.View;

import com.example.animationlibrary.library.Animators;
import com.example.mafei.viewmanager.manager.MyIntent;
import com.example.mafei.viewmanager.manager.ViewLaunchMode;
import com.example.mafei.viewmanager.manager
        .interfaces.IViewManager;
import com.example.mafei.viewmanager.view
        .interfaces.IBaseView;

/**
 * Created by jicool on 2017/1/12.
 */
public abstract class BaseView implements IBaseView {

    protected Context mContext;
    protected MyIntent mIntent;
    private boolean mDestroy;
    private boolean mVisible;
    protected View mView;
    protected IViewManager mViewManager;


    public BaseView(Context context, IViewManager viewManager) {
        mContext = context;
        mViewManager = viewManager;
    }


    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void setIntent(MyIntent intent) {
        mIntent = intent;
    }

    protected void startView(MyIntent intent) {
        mViewManager.startViewForResult(intent, -1);
    }

    protected void startViewForResult(MyIntent intent, int requestCode) {
        mViewManager.startViewForResult(intent, requestCode);
    }


    @Override
    public void createView(MyIntent intent) {
        if (intent != null) {
            mIntent = intent;
        }
        mView = createView();
        if (mView == null) {
            mView = new View(mContext);
        }
        mDestroy = false;
    }

    @Override
    public void resumeView() {
        mVisible = true;
    }

    @Override
    public void hideView() {
        mVisible = false;
//        mView.setVisibility(View.GONE);
    }

    @Override
    public void destroyView() {
        mDestroy = true;
        if (mIntent != null) {
            mIntent = null;
        }
    }

    public abstract View createView();

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public void setResult(int resultCode, MyIntent intent) {
        mViewManager.startViewWithResult(getRequestCode(), resultCode, mIntent);
    }

    @Override
    public void onViewResult(int requestCode, int resultCode, MyIntent intent) {

    }

    //    public boolean isDestroy() {
//        return mDestroy || ActivityUtils.isDestroyed(mContext);
//    }
    int mViewFlag;

    public int getViewFlag() {
        return mViewFlag;
    }

    public void setViewFlag(int flag) {
        mViewFlag = flag;
    }

    public ViewLaunchMode getLaunchMode() {
        return ViewLaunchMode.Standard;
    }


    private int mRequestCode = -1;

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
}
