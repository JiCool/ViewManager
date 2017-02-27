package com.example.mafei.viewmanager.manager;

/**
 * Created by jicool on 2017/1/12.
 */
public class ViewFlags {

    private static final int FLAG_INDEX = 0x1;

    /**
     * 当前入栈
     */
//    public static final int FLAG_ADD = FLAG_INDEX<<0;

    /**
     * 清空栈中当前信息，然后入栈
     */
//    public static final int FLAG_NEW = FLAG_INDEX<<1;

    public static final int FLAG_VIEW_NEW_TASK = FLAG_INDEX<<2;
    public static final int  FLAG_VIEW_CLEAR_TOP = FLAG_INDEX<<3;
    public static final int  FLAG_VIEW_NO_HISTORY = FLAG_INDEX<<4;
//    public static final int FLAG_VIEW_SINGLE_TOP = FLAG_INDEX <<5;

}
