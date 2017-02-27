package com.example.mafei.viewmanager.view.interfaces;

import com.example.mafei.viewmanager.manager.ViewUpdateType;

/**
 * Created by jicool on 2017/1/12.
 */
public interface IUpdateView {
    /**
     * 更新界面
     */
//    void update(IBaseView view);

    void update(IBaseView view, ViewUpdateType type);

    /**
     * 退出界面
     */
    void finishView();
}
