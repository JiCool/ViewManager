package com.example.mafei.viewmanager.view.navigator.interfaces;

import com.example.mafei.viewmanager.manager.MyIntent;
import com.example.mafei.viewmanager.view
        .interfaces.IBaseView;

/**
 * Created by jicool on 2017/2/13.
 */

public interface ITabHostContentView extends IBaseView {

    boolean isSelected();

    /**
     * 被切换到此subview
     */
    void onSelected(MyIntent intent);

    /**
     * 被切走
     */
    void onUnSelected();

}
