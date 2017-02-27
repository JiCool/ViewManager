package com.example.mafei.viewmanager.manager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.view.inputmethod
        .InputMethodManager;


import com.example.mafei.viewmanager.manager
        .interfaces.IViewManager;
import com.example.mafei.viewmanager.view
        .interfaces.IBaseView;
import com.example.mafei.viewmanager.view
        .interfaces.IUpdateView;

import java.lang.reflect.Constructor;
import java.lang.reflect
        .InvocationTargetException;
import java.util.Stack;

/**
 * Created by jicool on 2017/1/12.
 */
public class ViewManagerService implements IViewManager {
    private static final String TAG =
            "ViewManager";

    /**
     * 用户按home键，延迟一分钟杀死自身进程
     */
    private static final int
            ON_HOME_PRESSED_KILL_DELAY = 1 * 60
            * 1000;

    private static final int MSG_KILL_PROCESS = 1;

    /**
     * 视图栈，用于视图跳转
     */
    private Stack<Stack<IBaseView>> mStacks;

    /**
     * 当前视图栈
     */
    private Stack<IBaseView> mCurrentStack;

    // /**
    // * 缓存上次出栈的view,目前只缓存一个
    // */
    // private IBaseView mLastPopView;

    /**
     * 当前显示的view
     */
    private IBaseView mCurrView;

    /**
     * 当前activity的个数，指定为静态的，
     * 目前是全局的标示当前设置进程中的activity个数; 为0时，销毁
     */
    private static int mActivityCount;

    private Context mContext;

    private IUpdateView mIUpdateView;

    private BroadcastReceiver
            mHomeKeyEventReceiver = new
            BroadcastReceiver() {
                private static final String SYSTEM_REASON = "reason";
                private static final String
                        SYSTEM_HOME_KEY = "homekey";

                @Override
                public void onReceive(Context context,
                                      Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(Intent
                            .ACTION_CLOSE_SYSTEM_DIALOGS)) {
                        String reason = intent
                                .getStringExtra
                                        (SYSTEM_REASON);
                        if (TextUtils.equals(reason,
                                SYSTEM_HOME_KEY)) {
                            // 表示按了home键,程序到了后台
                            mHandler.sendEmptyMessageDelayed(MSG_KILL_PROCESS,
                                    ON_HOME_PRESSED_KILL_DELAY);
                        }
                    }
                }
            };

    private static Handler mHandler = new
            Handler() {

                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_KILL_PROCESS:
                            Process.killProcess(Process
                                    .myPid());
                            break;
                        default:
                            break;
                    }
                }
            };

    //public ClassLoader mClassLoader;
//    private static List<ViewEntity> views;
    public ViewManagerService(Context context,
                              IUpdateView updateView) {
        mContext = context;
        mIUpdateView = updateView;
//        views = ViewXmlResultManager
// .getConfigViews(mContext);
        mStacks = new Stack<>();
        registeHomeKeyReceiver();
        mActivityCount++;
    }

    private void registeHomeKeyReceiver() {
        mContext.registerReceiver
                (mHomeKeyEventReceiver, new
                        IntentFilter(Intent
                        .ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    private void unregisteHomeKeyReceiver() {
        //当mHomeKeyEventReceiver
        // 已经被反注册或者尚未注册时，调用反注册方法会抛出IllegalArgumentException
        //所以在这里添加异常捕获
        try {
            mContext.unregisterReceiver
                    (mHomeKeyEventReceiver);
        } catch (IllegalArgumentException e) {
        }
    }

    public void setIntent(MyIntent intent) {
        mCurrView.setIntent(intent);
    }

    public void resumeView() {
        mCurrView.resumeView();
    }


    public void destroyView() {
        // if (mLastPopView != null) {
        // mLastPopView.destroyView();
        // }
        unregisteHomeKeyReceiver();
        if (mCurrView != null) {
            mCurrView.destroyView();
        }
        if (mStacks != null) {
            int size = mStacks.size();

            for (int i = 0; i < size; i++) {
                Stack<IBaseView> stack = mStacks
                        .get(i);
                for (int j = 0; j < stack.size(); j++) {
                    IBaseView settingView = stack.get(i);
                    if (settingView != null) {
                        settingView.destroyView();
                    }
                }

            }
            mStacks.removeAllElements();
        }
        reduceAndcheckDestroySelf();
    }

    public void StopView() {
        if (mCurrView != null) {
            mCurrView.hideView();
        }
    }

    public void resumeView(MyIntent intent) {
        if (mCurrView == null) {
            return;
        }
        mCurrView.resumeView();
    }

    ViewLaunchMode getTaskLaunchMode(Stack<IBaseView> stack) {
        if (stack != null && stack.size() > 0) {
            return stack.get(0).getLaunchMode();
        }
        return ViewLaunchMode.Standard;
    }

    @Override
    public void startView(MyIntent intent) {

        if (intent == null || !Activity.class
                .isInstance(mContext)) {
            return;
        }

        IBaseView settingView = null;
        Class<?> cla = intent.getViewClass();
        if (intent.getComponent() != null) {
            String className = intent
                    .getComponent()
                    .getClassName();
            if (!((Activity) mContext)
                    .getComponentName()
                    .getClassName().equals
                            (className)) {
                destroyView();
                mContext.startActivity(intent);
                return;
            }
        }

        ViewLaunchMode launchMode = ViewLaunchMode.Standard;
        ViewLaunchMode taskLaunchMode = getTaskLaunchMode(mCurrentStack);


//        intent.getComponent().getClassName()
// .equals(mCurrView.get)
        int flag = intent.getViewFlag();
        int result = isViewExistInCurrentStack(cla);
        if (result >= 0) {
            //当前task中存在view，说明当前view的launchmode和task的launchmode要么相同，要么不同
            settingView = mCurrentStack.get
                    (result);
            launchMode = settingView.getLaunchMode();
            switch (taskLaunchMode) {
                case Standard:
                    switch (launchMode) {
                        case Standard:
                            switch (flag) {
                                case ViewFlags.FLAG_VIEW_NEW_TASK:
                                    if(result != mCurrentStack.size()-1)
                                    {
                                        settingView = makeNewView(cla,
                                                intent);
                                        settingView.setViewFlag(flag);
                                        mCurrentStack.push(settingView);
                                    }
                                    break;
                                case ViewFlags.FLAG_VIEW_CLEAR_TOP:
                                    for (int i = result + 1; i
                                            < mCurrentStack.size();
                                         i++) {
                                        IBaseView view =
                                                mCurrentStack.get
                                                        (mCurrentStack.size() - 1);
                                        view.destroyView();
                                        mCurrentStack.remove(mCurrentStack
                                                .size() - 1);
                                    }
                                    break;
                                case ViewFlags.FLAG_VIEW_NO_HISTORY:
                                    settingView = makeNewView(cla,
                                            intent);
                                    settingView.setViewFlag(flag);
                                    mCurrentStack.push(settingView);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case SingleInstance:
                        case SingleTask:
                            throw new RuntimeException("Task launch mode error");
                        default:
                            break;
                    }
                    break;
                case SingleTask:
                    for (int i = result + 1; i
                            < mCurrentStack.size();
                         i++) {
                        IBaseView view =
                                mCurrentStack.get
                                        (mCurrentStack.size() - 1);
                        view.destroyView();
                        mCurrentStack.remove(mCurrentStack
                                .size() - 1);
                    }
                    break;
                case SingleInstance:
                    for (int i = result + 1; i
                            < mCurrentStack.size();
                         i++) {
                        IBaseView view =
                                mCurrentStack.get
                                        (mCurrentStack.size() - 1);
                        view.destroyView();
                        mCurrentStack.remove(mCurrentStack
                                .size() - 1);
                    }
                    break;
                default:
                    break;
            }

        } else {
            int index = isViewExist(cla);
            if (index == -1) {
                if (mCurrentStack == null) {
                    mCurrentStack = new Stack<>();
                    mStacks.push(mCurrentStack);
                }
                settingView = makeNewView(cla,
                        intent);
                if(settingView == null)
                {
                    return;
                }
                settingView.setViewFlag(flag);
                launchMode = settingView.getLaunchMode();
                switch (taskLaunchMode) {
                    case Standard:
                        switch (launchMode) {
                            case Standard:
                                mCurrentStack.push(settingView);
                                break;
                            case SingleTask:
                                if(mCurrentStack!=null &&mCurrentStack.isEmpty())
                                {
                                    mCurrentStack.push(settingView);
                                }
                                else
                                {
                                    Stack<IBaseView> stack = new Stack<>();
                                    stack.push(settingView);
                                    mStacks.push(stack);
                                    mCurrentStack = stack;
                                }

                                break;
                            case SingleInstance:
                                if(mCurrentStack!=null &&mCurrentStack.isEmpty())
                                {
                                    mCurrentStack.push(settingView);
                                }
                                else
                                {
                                    Stack<IBaseView> stack_1 = new Stack<>();
                                    stack_1.push(settingView);
                                    mStacks.push(stack_1);
                                    mCurrentStack = stack_1;
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    case SingleTask:
                        switch (launchMode) {
                            case Standard:
                                mCurrentStack.push(settingView);
                                break;
                            case SingleTask:
                                Stack<IBaseView> stack = new Stack<>();
                                stack.push(settingView);
                                mStacks.push(stack);
                                mCurrentStack = stack;
                                break;
                            case SingleInstance:
                                Stack<IBaseView> stack_1 = new Stack<>();
                                stack_1.push(settingView);
                                mStacks.push(stack_1);
                                mCurrentStack = stack_1;
                                break;
                            default:
                                break;
                        }
                        break;
                    case SingleInstance:
                        Stack<IBaseView> stack = new Stack<>();
                        stack.push(settingView);
                        mStacks.push(stack);
                        mCurrentStack = stack;
                        break;
                    default:
                        break;
                }

            } else {
                int stack_index = index >> 4;
                int view_index = index & 0x01;
                IBaseView tempView = mStacks.get(stack_index).get(view_index);
                if (tempView != null) {
                    if (tempView.getLaunchMode() == ViewLaunchMode.SingleInstance || tempView.getLaunchMode() == ViewLaunchMode.SingleTask) {
                        mCurrentStack = mStacks.get(stack_index);
                        mStacks.remove(stack_index);
                        mStacks.push(mCurrentStack);
                        for (int i = view_index + 1; i
                                < mCurrentStack.size();
                             i++) {
                            IBaseView view =
                                    mCurrentStack.get
                                            (mCurrentStack.size() - 1);
                            view.destroyView();
                            mCurrentStack.remove(mCurrentStack
                                    .size() - 1);
                        }
                    }
                }

            }
        }
        if (mCurrView != null) {
            mCurrView.hideView();
            if (mCurrentStack == null) {
                mCurrentStack = new
                        Stack<IBaseView>();
                mStacks.push(mCurrentStack);
            }
            if (mCurrView.getViewFlag() == ViewFlags.FLAG_VIEW_NO_HISTORY) {
                mCurrView.destroyView();
//                mCurrentStack.remove(mCurrentStack.size() - 2);
            }
        }

//        switch (flag) {
//            case ViewFlags.FLAG_ADD:
//                if (result >= 0) {
//                    // 在stack中
//                    // 此view后的view都去除
//                    for (int i = result + 1; i
//                            < mStacks.size();
//                         i++) {
//                        IBaseView view =
//                                mStacks.get
//                                        (mStacks.size() - 1);
//                        view.destroyView();
//                        mStacks.remove(mStacks
//                                .size() - 1);
//                    }
//                    // 此view也移除
//                    settingView = mStacks.get
//                            (result);
//                    mStacks.remove(result);
//                }
//
//                if (mCurrView != null) {
//                    mCurrView.hideView();
//
//                    if (mStacks == null) {
//                        mStacks = new
//                                Stack<IBaseView>();
//                    }
//                    mStacks.push(mCurrView);
//                }
//                break;
//
//            case ViewFlags.FLAG_NEW:
//                if (result >= 0) {
//                    // 在stack中
//                    settingView = mStacks.get
//                            (result);
//                }
//
//                if (mCurrView != null) {
//                    mCurrView.hideView();
//                    mCurrView.destroyView();
//
//                    mCurrView = null;
//                }
//
//                // 将栈中的其他view销毁
//                if (mStacks != null && !mStacks
//                        .isEmpty()) {
//                    for (IBaseView view :
//                            mStacks) {
//                        if (result >= 0) {
//                            if (view !=
//                                    settingView) {
//                                view.destroyView();
//                            }
//                        } else {
//                            view.destroyView();
//                        }
//                    }
//                    mStacks.removeAllElements();
//                }
//                break;
//            default:
//                break;
//        }

        // 是上次弹出的
//        if (result == -1) {
//            // settingView = mLastPopView;
//            // mLastPopView = null;
//        }

        settingView = mCurrentStack.peek();
        // 创建或者刷新
        if(settingView.getViewFlag() == ViewFlags.FLAG_VIEW_NO_HISTORY)
        {
            mCurrentStack.pop();
        }

        if (settingView != null) {

            settingView.createView(intent);
            mCurrView = settingView;
            mIUpdateView.update(mCurrView, ViewUpdateType.Enter);
            mCurrView.resumeView();
        }

    }

    @Override
    public void startViewForResult(MyIntent intent, int requestCode) {
        if (intent == null || !Activity.class
                .isInstance(mContext)) {
            return;
        }

        IBaseView settingView = null;
        Class<?> cla = intent.getViewClass();
        if (intent.getComponent() != null) {
            String className = intent
                    .getComponent()
                    .getClassName();
            if (!((Activity) mContext)
                    .getComponentName()
                    .getClassName().equals
                            (className)) {
                destroyView();
                mContext.startActivity(intent);
                return;
            }
        }

        ViewLaunchMode launchMode = ViewLaunchMode.Standard;
        ViewLaunchMode taskLauncheMode = getTaskLaunchMode(mCurrentStack);


//        intent.getComponent().getClassName()
// .equals(mCurrView.get)
        int flag = intent.getViewFlag();
        int result = isViewExistInCurrentStack(cla);
        if (result >= 0) {
            //当前task中存在view，说明当前view的launchmode和task的launchmode要么相同，要么不同
            settingView = mCurrentStack.get
                    (result);
            launchMode = settingView.getLaunchMode();
            switch (taskLauncheMode) {
                case Standard:
                    switch (launchMode) {
                        case Standard:
                            switch (flag) {
                                case ViewFlags.FLAG_VIEW_NEW_TASK:
                                    if(result != mCurrentStack.size()-1)
                                    {
                                        settingView = makeNewView(cla,
                                                intent);
                                        settingView.setViewFlag(flag);
                                        mCurrentStack.push(settingView);
                                    }
                                    break;
                                case ViewFlags.FLAG_VIEW_CLEAR_TOP:
                                    for (int i = result + 1; i
                                            < mCurrentStack.size();
                                         i++) {
                                        IBaseView view =
                                                mCurrentStack.get
                                                        (mCurrentStack.size() - 1);
                                        view.destroyView();
                                        mCurrentStack.remove(mCurrentStack
                                                .size() - 1);
                                    }
                                    break;
                                case ViewFlags.FLAG_VIEW_NO_HISTORY:
                                    settingView = makeNewView(cla,
                                            intent);
                                    settingView.setViewFlag(flag);
                                    mCurrentStack.push(settingView);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case SingleInstance:
                        case SingleTask:
                            throw new RuntimeException("Task launch mode error");
                        default:
                            break;
                    }
                    break;
                case SingleTask:
                    for (int i = result + 1; i
                            < mCurrentStack.size();
                         i++) {
                        IBaseView view =
                                mCurrentStack.get
                                        (mCurrentStack.size() - 1);
                        view.destroyView();
                        mCurrentStack.remove(mCurrentStack
                                .size() - 1);
                    }
                    break;
                case SingleInstance:
                    for (int i = result + 1; i
                            < mCurrentStack.size();
                         i++) {
                        IBaseView view =
                                mCurrentStack.get
                                        (mCurrentStack.size() - 1);
                        view.destroyView();
                        mCurrentStack.remove(mCurrentStack
                                .size() - 1);
                    }
                    break;
                default:
                    break;
            }

        } else {
            int index = isViewExist(cla);
            if(index >-1)
            {
                int stack_index = index >> 4;
                int view_index = index & 0x01;
                IBaseView tempView = mStacks.get(stack_index).get(view_index);
                if (tempView != null) {
                    if (tempView.getLaunchMode() == ViewLaunchMode.SingleInstance || tempView.getLaunchMode() == ViewLaunchMode.SingleTask) {
                        mCurrentStack = mStacks.get(stack_index);
                        mStacks.remove(stack_index);
                        mStacks.push(mCurrentStack);
                        for (int i = view_index + 1; i
                                < mCurrentStack.size();
                             i++) {
                            IBaseView view =
                                    mCurrentStack.get
                                            (mCurrentStack.size() - 1);
                            view.destroyView();
                            mCurrentStack.remove(mCurrentStack
                                    .size() - 1);
                        }
                        mCurrView = tempView;
                    }
                    else
                    {
                        index = -1;
                    }
                }
            }
            if (index == -1) {
                if (mCurrentStack == null) {
                    mCurrentStack = new Stack<>();
                    mStacks.push(mCurrentStack);
                }
                settingView = makeNewView(cla,
                        intent);
                settingView.setViewFlag(flag);
                launchMode = settingView.getLaunchMode();
                switch (taskLauncheMode) {
                    case Standard:
                        switch (launchMode) {
                            case Standard:
                                mCurrentStack.push(settingView);
                                break;
                            case SingleTask:
                                Stack<IBaseView> stack = new Stack<>();
                                stack.push(settingView);
                                mStacks.push(stack);
                                mCurrentStack = stack;
                                break;
                            case SingleInstance:
                                Stack<IBaseView> stack_1 = new Stack<>();
                                stack_1.push(settingView);
                                mStacks.push(stack_1);
                                mCurrentStack = stack_1;
                                break;
                            default:
                                break;
                        }
                        break;
                    case SingleTask:
                        switch (launchMode) {
                            case Standard:
                                mCurrentStack.push(settingView);
                                break;
                            case SingleTask:
                                Stack<IBaseView> stack = new Stack<>();
                                stack.push(settingView);
                                mStacks.push(stack);
                                mCurrentStack = stack;
                                break;
                            case SingleInstance:
                                Stack<IBaseView> stack_1 = new Stack<>();
                                stack_1.push(settingView);
                                mStacks.push(stack_1);
                                mCurrentStack = stack_1;
                                break;
                            default:
                                break;
                        }
                        break;
                    case SingleInstance:
                        Stack<IBaseView> stack = new Stack<>();
                        stack.push(settingView);
                        mStacks.push(stack);
                        mCurrentStack = stack;
                        break;
                    default:
                        break;
                }

            } else {
                int stack_index = index >> 4;
                int view_index = index & 0x01;
                IBaseView tempView = mStacks.get(stack_index).get(view_index);
                if (tempView != null) {
                    if (tempView.getLaunchMode() == ViewLaunchMode.SingleInstance || tempView.getLaunchMode() == ViewLaunchMode.SingleTask) {
                        mCurrentStack = mStacks.get(stack_index);
                        mStacks.remove(stack_index);
                        mStacks.push(mCurrentStack);
                        for (int i = view_index + 1; i
                                < mCurrentStack.size();
                             i++) {
                            IBaseView view =
                                    mCurrentStack.get
                                            (mCurrentStack.size() - 1);
                            view.destroyView();
                            mCurrentStack.remove(mCurrentStack
                                    .size() - 1);
                        }
                    }
                }

            }
        }
        if (mCurrView != null) {
            mCurrView.hideView();
            if (mCurrentStack == null) {
                mCurrentStack = new
                        Stack<IBaseView>();
                mStacks.push(mCurrentStack);
            }
            if (mCurrView.getViewFlag() == ViewFlags.FLAG_VIEW_NO_HISTORY) {
                mCurrView.destroyView();
//                mCurrentStack.remove(mCurrentStack.size() - 2);
            }
        }


        settingView = mCurrentStack.peek();
        // 创建或者刷新
        if(settingView.getViewFlag() == ViewFlags.FLAG_VIEW_NO_HISTORY)
        {
            mCurrentStack.pop();
        }

        if (settingView != null) {
            if(requestCode >= 0)
            {
                settingView.setRequestCode(requestCode);
            }
            settingView.createView(intent);
            mCurrView = settingView;
            mIUpdateView.update(mCurrView, ViewUpdateType.Enter);
            mCurrView.resumeView();
        }
    }

    @Override
    public void startViewWithResult(int requestCode,int resultCode, MyIntent intent) {
        startViewForResult(intent,-1);
        mCurrView.onViewResult(requestCode,resultCode,intent);
    }

    /**
     * activity销毁时主动调用
     */
    public static void
    reduceAndcheckDestroySelf() {
        mActivityCount--;
        if (mActivityCount <= 0) {
            mHandler.sendEmptyMessage
                    (MSG_KILL_PROCESS);
        }
    }

    public static void removeKillSelf() {
        mHandler.removeMessages(MSG_KILL_PROCESS);
    }

    public static void addActivityCount() {
        mActivityCount++;

    }

    /**
     * 处理返回键；true，处理；false，不处理，上层处理
     *
     * @return
     */
    public boolean onBackPressed(MyIntent intent) {
        hideIme(mContext);
        if (mStacks == null || mStacks.isEmpty
                () || mStacks.size() == 0) {
            if (mIUpdateView != null) {
                mIUpdateView.finishView();
            }
            return false;
        }

        if (mCurrView == null) {
            if (mIUpdateView != null) {
                mIUpdateView.finishView();
            }
            return false;
        }
        if (!mCurrentStack.empty()&&mCurrentStack.peek().equals(mCurrView)) {
            mCurrentStack.pop();
        }

        // 上次界面销毁
        if (mCurrView != null) {
            mCurrView.hideView();
            mCurrView.destroyView();
            mCurrView = null;
        }

        // // 当前界面缓存并隐藏
        // mLastPopView = mCurrView;
        // mLastPopView.hideView();
        // mLastPopView.startAnimation
        // (ViewAnimUtil.ACTION_EXIT);
        if (!mCurrentStack.empty()) {
            // 弹出新的界面作为当前
            mCurrView = mCurrentStack.pop();
        }

        while (mCurrView == null && mStacks != null && !mStacks.empty()) {
            mCurrentStack = mStacks.pop();
            if (mCurrentStack != null && !mCurrentStack.empty()) {
                mCurrView = mCurrentStack.pop();
            }
        }

//        if (mCurrView.getView() == null &&
// mCurrView.getViewType() == ViewType
// .OPERATION_BACK_VIEW) {
//            if (mIUpdateView != null) {
//                mIUpdateView.finishView();
//            }
//            return false;
//        }
        if(mCurrView == null)
        {
            if (mIUpdateView != null) {
                mIUpdateView.finishView();
            }
            return false;
        }

        mIUpdateView.update(mCurrView, ViewUpdateType.Exit);
        // FIXME
        // 先更新view，再resume：resume中元素需要依赖view
        // 先设置成功，比如preference中

        mCurrView.resumeView();
        return true;
    }

    /**
     * 隐藏输入法软键盘
     *
     * @param mContext
     */
    public void hideIme(Context mContext) {
        try {
            InputMethodManager
                    inputMethodManager =
                    (InputMethodManager)
                            mContext.getApplicationContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
            inputMethodManager
                    .hideSoftInputFromWindow((
                            (Activity)
                                    mContext)
                            .getWindow()
                            .getDecorView()
                            .getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    /**
     * 判断指定类型是否存在：存在，返回stack索引 << 4 + stack内部索引；否则，-1
     *
     * @param
     * @return
     */
    private int isViewExist(Class<?> cla) {
        int index = -1;
        if (mStacks == null || mStacks.isEmpty
                ()) {
            return index;
        }
        int size = mStacks.size();
        for (int i = size-1; i >= 0; i--) {

            Stack<IBaseView> stack = mStacks.get(i);
            if(stack.isEmpty())
            {
                continue;
            }
            for (int j = stack.size() -1; j >=0; j--) {
                if (stack.get(j).getClass().getName().equals(cla.getName())) {
                    index = (i << 4) + j;
                    return  index;
                }
            }
        }
        return index;
    }

    /**
     * 判断指定类型是否存在：存在，返回index；否则，-1
     *
     * @param
     * @return
     */
    private int isViewExistInCurrentStack(Class<?> cla) {
        int index = -1;
        if (mCurrentStack == null || mCurrentStack.isEmpty
                ()) {
            return index;
        }
//        int mainType = cla.getClass().hashCode();
        int size = mCurrentStack.size();
        for (int i = size -1 ; i >= 0; i--) {
            if (mCurrentStack.get(i).getClass().getName().equals(cla.getName())) {
                index = i;
                return index;
            }
        }
        return index;
    }

    /**
     * 根据type生成新的设置view
     *
     * @param
     * @return
     */
    private IBaseView makeNewView(Class<?> cla,
                                  Intent intent) {
        if (cla == null) {
            throw new IllegalArgumentException();
        }
        IBaseView baseView = null;
        try {

//            baseView = new ((IBaseView)
//                    cla)(mContext);
             /*以下调用带参的、私有构造函数*/
            Constructor c1 = cla
                    .getDeclaredConstructor(new
                            Class[]{Context
                            .class,
                            IViewManager.class});
            c1.setAccessible(true);
            baseView = (IBaseView) c1
                    .newInstance(mContext, this);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
//        int mainType = ViewType.getMainType
// (cla);
//
//        switch (mainType) {
//            case ViewType.SPLASH:
////                Class<?> c = null;
//                try {
//
//                        baseView = (IBaseView)
//                                    cla
// .newInstance();
//                }catch
// (InstantiationException e) {
//                    e.printStackTrace();
//                } catch
// (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//
//
////                settingView = new
// WizardView(mContext, this);
//                break;
//
//            default:
//
//                break;
//        }
        return baseView;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (mCurrView != null) {
            mCurrView.onWindowFocusChanged
                    (hasFocus);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (mCurrView != null) {
            mCurrView
                    .onRequestPermissionsResult
                            (requestCode,
                                    permissions, grantResults);
        }
    }

    public void onLowMemory() {


        if (mStacks == null) {
            return;
        }

        int size = mStacks.size();

        if (size <= 1) {
            return;
        }
        int deletCnt = size - 1;
        for (int i = 0; i < deletCnt; i++) {
            Stack<IBaseView> stack = mStacks.get(0);
            while (!stack.empty()) {
                IBaseView view = stack.pop();
                if (view != null) {
                    view.hideView();
                    view.destroyView();
                }
            }
            mStacks.remove(0);
        }

    }

}
