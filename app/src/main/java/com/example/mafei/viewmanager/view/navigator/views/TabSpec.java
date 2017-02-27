package com.example.mafei.viewmanager.view.navigator.views;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by jicool on 2017/2/24.
 */

public class TabSpec extends LinearLayout {

    protected LinearLayout mContentView;
    protected Context mContext;
    public TabSpec(Context context) {
        super(context);
        mContext = context;
        setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
        setOrientation(VERTICAL);
        initContainer();
    }

    public void setIndicator(View view) {
        mContentView.removeAllViews();
        mContentView.addView(view);
    }

    private void initContainer() {

        mContentView = new LinearLayout(mContext);
        LayoutParams contentLayoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        mContentView.setLayoutParams(contentLayoutParams);
        mContentView.setGravity(Gravity.CENTER);
        mContentView.setOrientation(VERTICAL);
        addView(mContentView);
    }

    public void setState(boolean selected) {
        if (selected) {
            setSelected(true);
            mContentView.setSelected(true);
        } else {
            setSelected(true);
            mContentView.setSelected(false);
        }
    }

}
