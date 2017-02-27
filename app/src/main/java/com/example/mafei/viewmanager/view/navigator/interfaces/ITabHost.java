package com.example.mafei.viewmanager.view.navigator.interfaces;

import com.example.mafei.viewmanager.view.navigator.impl.BaseTabHostContentView;
import com.example.mafei.viewmanager.view.navigator.views.TabSpec;
import com.example.mafei.viewmanager.manager.MyIntent;
import com.example.mafei.viewmanager.view.interfaces.IBaseView;

/**
 * Created by jicool on 2017/2/24.
 */

public interface ITabHost extends IBaseView{
    void addTabContentView(TabSpec tabSpec, Class<BaseTabHostContentView> cla);

    void addTabContentView(int index, TabSpec tabSpec, Class<BaseTabHostContentView> cla);

    void removeTabContentView(BaseTabHostContentView tabContentView);

    void removeAllTabContentView();

    void removeTabContentView(int index);

    void switchToTabContentView(int index);

    void switchToTabContentView(int index, MyIntent intent);

}
