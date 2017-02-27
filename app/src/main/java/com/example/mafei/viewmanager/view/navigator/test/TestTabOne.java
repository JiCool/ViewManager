package com.example.mafei.viewmanager.view.navigator.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.mafei.viewmanager.R;
import com.example.mafei.viewmanager.manager.interfaces.IViewManager;
import com.example.mafei.viewmanager.view.navigator.impl.BaseTabHostContentView;

/**
 * Created by jicool on 2017/2/25.
 */

public class TestTabOne extends BaseTabHostContentView {

    public TestTabOne(Context context, IViewManager viewManager) {
        super(context,viewManager);
        mContext = context;

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mContentView = (LinearLayout) layoutInflater.inflate(R.layout.subview_one, null);
//        mThemeTabView = new PageTabView(mContext, PageTabView.STYLE_TITLE_TOP);
//        mThemeTabView.setTabTitleStyle(TabTitleBuildFactory.STYLE_SUB_UI);
//        mThemeTabView.setOnPageChangeListener(this);
//
//        mMainService = (MainAbilityService) AppComm.register(mContext, BusinessServiceName.MAIN_SERVICE);
//        mAssistService = (AssistProcessService) AppComm.register(mContext, BusinessServiceName.BACKGROUD_SERVICE);
//
//        mSkinDataManager = (SettingSkinDataService) AppComm.register(mContext,
//                BusinessServiceName.SETTING_SKIN_SERVICE);
//
//        buildTabView();
    }
}
