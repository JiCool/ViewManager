package com.example.mafei.viewmanager.view.navigator.scrolltab.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.mafei.viewmanager.R;
import com.example.mafei.viewmanager.manager.interfaces.IViewManager;
import com.example.mafei.viewmanager.view.navigator.impl.BaseTabHostContentView;

/**
 * Created by jicool on 2017/2/20.
 */

public class ScrollSubTabView2  extends BaseTabHostContentView {

    public ScrollSubTabView2(Context context, IViewManager viewManager)
    {
        super(context,viewManager);

//        mUIHandler = new ScrollSubTabView1.UIHandler(this);
        initView();
    }



    private void initView() {
        if (mContentView != null) {
            return;
        }
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mContentView = (LinearLayout) layoutInflater.inflate(R.layout.scrollsubtabview2, null);
    }


}
