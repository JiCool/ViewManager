package com.example.mafei.viewmanager.manager.interfaces;

import com.example.mafei.viewmanager.manager.MyIntent;

/**
 * Created by jicool on 2017/1/12.
 */
public interface IViewManager {


    /**
     * 切换界面
     *
     * @param cla
     *            切换到指定类型的view
     * @param flag
     *            切换类型，add or new
     */
    void startView(MyIntent intent);
    void startViewForResult(MyIntent intent, int requestCode);
    void startViewWithResult(int requestCode,int resultCode,MyIntent intent);
    boolean onBackPressed(MyIntent intent);
}
