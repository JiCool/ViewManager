package com.example.mafei.viewmanager.parser.entity;

/**
 * Created by jicool on 2017/1/12.
 */
public class ViewEntity {
    String mClassName;
    String mFlag;

    public String getViewType() {
        return ViewType;
    }

    public void setViewType(String viewType) {
        ViewType = viewType;
    }

    public String getmFlag() {
        return mFlag;
    }

    public void setmFlag(String mFlag) {
        this.mFlag = mFlag;
    }

    String ViewType;

    public String getmClassName() {
        return mClassName;
    }

    public void setmClassName(String mClassName) {
        this.mClassName = mClassName;
    }

    @Override
    public String toString() {
        return "ViewEntity{" +
                "mClassName='" + mClassName +
                '\'' +
                ", mFlag='" + mFlag + '\'' +
                ", ViewType='" + ViewType + '\'' +
                '}';
    }
}
