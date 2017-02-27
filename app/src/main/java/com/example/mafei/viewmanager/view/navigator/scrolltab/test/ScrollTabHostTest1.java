package com.example.mafei.viewmanager.view.navigator.scrolltab.test;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mafei.viewmanager.R;
import com.example.mafei.viewmanager.manager.interfaces.IViewManager;
import com.example.mafei.viewmanager.view.navigator.impl.BaseTabHostContentView;
import com.example.mafei.viewmanager.view.navigator.impl.TabHost;
import com.example.mafei.viewmanager.view.navigator.scrolltab.ScrollTabHost;
import com.example.mafei.viewmanager.view.navigator.scrolltab.viewpager.ViewPager;
import com.example.mafei.viewmanager.view.navigator.views.TabSpec;
import com.example.mafei.viewmanager.manager.MyIntent;

/**
 * Created by jicool on 2017/2/27.
 */

public class ScrollTabHostTest1 extends BaseTabHostContentView implements ViewPager.OnPageChangeListener {

    private ScrollTabHost mScrollTabHost;
    MyIntent mIntent = null;
    private int mLastIndex;

    private String texts[] = {"测试1", "测试2"};
    private Class TabArray[] = {ScrollSubTabView1.class, ScrollSubTabView2.class};
    private int imageButton[] = {R.drawable.selector_me, R.drawable.selector_bill};

    public ScrollTabHostTest1(Context context, IViewManager viewManager) {
        super(context, viewManager);
        mScrollTabHost = new ScrollTabHost(mContext, viewManager, TabHost.STYLE_TITLE_TOP);
        mScrollTabHost.setOnPageChangeListener(this);
        initView();
    }
    @Override
    public void resumeView() {
        super.resumeView();
        mScrollTabHost.switchToTabContentView(0);
    }
    private void initView() {
        if(mScrollTabHost.isEmpty())
        {
            for (int i = 0; i < texts.length; i++) {
                TabSpec tabSpec = mScrollTabHost.newTabSpec();
                tabSpec.setIndicator(getView(i, mContext));
                mScrollTabHost.addTabContentView(tabSpec, TabArray[i]);
            }
        }
        mScrollTabHost.switchToTabContentView(0);

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
        imageView.setVisibility(View.GONE);
        //设置标题
        textView.setText(texts[i]);
        return view;
    }

    @Override
    public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {

    }

    @Override
    public void onPageSelected(int paramInt) {

    }

    @Override
    public void onPageScrollStateChanged(int paramInt) {

    }
    @Override
    public View getView() {
        return mScrollTabHost;
    }
}
