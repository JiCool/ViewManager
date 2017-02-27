package com.example.mafei.viewmanager.view.interfaces;


import android.view.View;

import com.example.animationlibrary.library.Animators;
import com.example.mafei.viewmanager.manager.MyIntent;
import com.example.mafei.viewmanager.manager.ViewLaunchMode;

/**
 * Created by jicool on 2017/1/12.
 */
public interface IBaseView {


    /**
     * 获取view
     *
     * @return
     */
    View getView();



    /**
     * 初始化创建view
     *
     * @param intent
     * @return
     */
    void createView(MyIntent intent);

    /**
     * 界面恢复
     */
    void resumeView();

    /**
     * 界面隐藏
     */
    void hideView();

    /**
     * 界面销毁时处理资源回收
     */
    void destroyView();

//    View createView();
    /**
     * 界面焦点改变
     *
     * @param hasFocus
     */
    void onWindowFocusChanged(boolean hasFocus);

    /**
     * 权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

     void  setIntent(MyIntent intent);

    ViewLaunchMode getLaunchMode();

    int getViewFlag();
    void setViewFlag(int flag);

    void setResult(int resultCode,MyIntent intent);
    void onViewResult(int requestCode, int resultCode, MyIntent intent);

    void setRequestCode(int requestCode);
    int getRequestCode();

    Animators getEnterAnimator();
    Animators getExitAnimator();
}
