package com.example.mafei.viewmanager.manager;

import android.content.Intent;
import android.os.Bundle;

import com.example.mafei.viewmanager.manager
        .ViewFlags;

/**
 * Created by jicool on 2017/2/14.
 */

public class MyIntent extends Intent {
    public MyIntent(){
        super();
    }
    public MyIntent(Intent intent){
       super(intent);
    }

    public Class<?> getViewClass() {
        if(mViewClass == null)
        {
            throw new RuntimeException();
        }
        return mViewClass;
    }

    public void setViewClass(Class<?>
                                         mSubViewClass) {
        this.mViewClass = mSubViewClass;
    }

    Class<?> mViewClass;

    public int getViewFlag() {
        return mViewFlag;
    }

    public void setViewFlag(int mSubViewFlag) {
        this.mViewFlag = mSubViewFlag;
    }

    int mViewFlag = ViewFlags.FLAG_VIEW_NEW_TASK;

    public Bundle getViewBundle() {
        return mViewBundle;
    }

    public void setViewBundle(Bundle mViewBundle) {
        this.mViewBundle = mViewBundle;
    }

    Bundle mViewBundle = new Bundle();

}
