package com.example.mafei.viewmanager.view.navigator.scrolltab.viewpager;

import android.os.Parcelable;
import android.view.View;

/**
 * Created by jicool on 2017/2/13.
 */

public abstract class PagerAdapter {

    private DataSetObserver mObserver;
    public static final int POSITION_UNCHANGED = -1;
    public static final int POSITION_NONE = -2;

    public abstract int getCount();

    public abstract void startUpdate(View paramView);

    public abstract Object instantiateItem(View paramView, int paramInt);

    public abstract void destroyItem(View paramView, int paramInt,
                                     Object paramObject);

    public abstract void finishUpdate(View paramView);

    public abstract boolean isViewFromObject(View paramView, Object paramObject);

    public abstract Parcelable saveState();

    public abstract void restoreState(Parcelable paramParcelable,
                                      ClassLoader paramClassLoader);

    public int getItemPosition(Object object) {
        return -1;
    }

    public void notifyDataSetChanged() {
        if (this.mObserver != null)
            this.mObserver.onDataSetChanged();
    }

    void setDataSetObserver(DataSetObserver observer) {
        this.mObserver = observer;
    }

    static abstract interface DataSetObserver {
        public abstract void onDataSetChanged();
    }
}
