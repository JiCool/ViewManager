package com.example.mafei.viewmanager.view.navigator.test;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mafei.viewmanager.R;
import com.example.mafei.viewmanager.manager.interfaces.IViewManager;
import com.example.mafei.viewmanager.view.navigator.views.TabSpec;
import com.example.mafei.viewmanager.view.navigator.impl.TabHost;
import com.example.mafei.viewmanager.view.navigator.scrolltab.test.ScrollTabHostTest1;
import com.example.mafei.viewmanager.view.impl.BaseView;

/**
 * Created by jicool on 2017/2/25.
 */

public class TabHostTest extends BaseView {

    private String texts[] = {"我", "订单"};
    private Class TabArray[] = {TestTabOne.class, ScrollTabHostTest1.class};
    private int imageButton[] = {R.drawable.selector_me, R.drawable.selector_bill};
    TabHost mTabHost;

    public TabHostTest(Context context, IViewManager viewManager) {
        super(context, viewManager);
        mTabHost = new TabHost(context, viewManager, 0);
    }


    @Override
    public View createView() {
        for (int i = 0; i < texts.length; i++) {
            TabSpec tabSpec = mTabHost.newTabSpec();
//            tabSpec.setText(texts[i]);
//            Debug.waitForDebugger();
            tabSpec.setIndicator(getView(i, mContext));
//            Debug.waitForDebugger();
            mTabHost.addTabContentView(tabSpec, TabArray[i]);
        }

        return mTabHost;
    }

    @Override
    public void resumeView() {
        super.resumeView();
        mTabHost.switchToTabContentView(0);
    }

    private View getView(int i, Context context) {
        //取得布局实例
        View view = View.inflate(context, R.layout.tabtitle, null);

        //取得布局对象
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        TextView textView = (TextView) view.findViewById(R.id.text);
        //设置图标
//        imageView.setBackgroundResource(imageButton[i]);
        imageView.setImageResource(imageButton[i]);
        //设置标题
        textView.setText(texts[i]);
        return view;
    }
}
