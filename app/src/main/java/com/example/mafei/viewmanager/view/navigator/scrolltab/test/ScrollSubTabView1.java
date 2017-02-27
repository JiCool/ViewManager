package com.example.mafei.viewmanager.view.navigator.scrolltab.test;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.mafei.viewmanager.R;
import com.example.mafei.viewmanager.manager.interfaces.IViewManager;
import com.example.mafei.viewmanager.view.navigator.impl.BaseTabHostContentView;
import com.example.mafei.viewmanager.manager.MyIntent;

import java.lang.ref.WeakReference;

/**
 * Created by jicool on 2017/2/20.
 */

public class ScrollSubTabView1 extends BaseTabHostContentView {
    private final UIHandler mUIHandler;

public ScrollSubTabView1(Context context, IViewManager viewManager)
{
    super(context,viewManager);
    mUIHandler = new UIHandler(this);
//    initView();
}
    private static class UIHandler extends Handler {

        private WeakReference<ScrollSubTabView1> mPReference;

        public UIHandler(ScrollSubTabView1 impl) {
            super(Looper.getMainLooper());
            mPReference = new WeakReference<ScrollSubTabView1>(impl);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            ScrollSubTabView1 impl = mPReference.get();
            if (impl == null) {
                return;
            }

            switch (msg.what) {

                default:
                    break;
            }
        };
    }


    private void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mContentView = (LinearLayout) layoutInflater.inflate(R.layout.scrollsubtabview1, null);
    }

    @Override
    public void createView(MyIntent intent) {
        super.createView(intent);
        initView();
    }
}
