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

public class TestTabTwo extends BaseTabHostContentView {
    public TestTabTwo(Context context, IViewManager viewManager) {
        super(context, viewManager);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mContentView = (LinearLayout) layoutInflater.inflate(R.layout.subtabview_two, null);
    }
}
