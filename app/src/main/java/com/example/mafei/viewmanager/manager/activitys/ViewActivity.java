package com.example.mafei.viewmanager.manager.activitys;

//import android.animation.Animator;
//import android.animation.LayoutTransition;
//import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

import com.example.animationlibrary.library.AnimatorsManager;
import com.example.mafei.viewmanager.manager.ViewManagerService;
import com.example.mafei.viewmanager.manager.MyIntent;
import com.example.mafei.viewmanager.manager.ViewUpdateType;
import com.example.mafei.viewmanager.view
        .interfaces.IBaseView;
import com.example.mafei.viewmanager.view
        .interfaces.IUpdateView;
import com.nineoldandroids.animation.Animator;

public class ViewActivity extends Activity implements IUpdateView {


    private static final String TAG = "BaseActivity";

    private ViewGroup mLayout;
    private ViewManagerService mViewManager;
    private Animator.AnimatorListener enterAnimatorListener =new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mLayout.removeViewAt(0);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mLayout.removeViewAt(0);
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private Animator.AnimatorListener exitAnimatorListener =new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if(mLayout.getChildCount()>=2)
            {
                mLayout.removeViewAt(1);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            if(mLayout.getChildCount()>=2)
            {
                mLayout.removeViewAt(1);
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
    //    private LayoutTransition mTransitioner;

    @SuppressWarnings("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayout = new RelativeLayout(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mLayout.setLayoutParams(params);
        mLayout.setBackgroundColor(android.R.color.transparent);
        setContentView(mLayout);
        mViewManager = new ViewManagerService(this, this);
//        mTransitioner = new LayoutTransition();
        //入场动画:view在这个容器中消失时触发的动画
//        ObjectAnimator animIn = ObjectAnimator.ofFloat(null, "rotationY", 0f, 360f,0f);
//        animIn.setDuration(500);
//        mTransitioner.setAnimator(LayoutTransition.APPEARING, animIn);

        //出场动画:view显示时的动画
//        ObjectAnimator animOut = ObjectAnimator.ofFloat(null, "rotation", 0f, 90f, 0f);
//        mTransitioner.setAnimator(LayoutTransition.DISAPPEARING, animOut);

//        mLayout.setLayoutTransition(mTransitioner);
    }

   protected void startViewActivity(MyIntent intent) {
//       Debug.waitForDebugger();
        mViewManager.startView(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mViewManager.resumeView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mViewManager.setIntent(new MyIntent(intent));
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewManager.StopView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewManager.destroyView();
    }

    /**
     * 解决某些手机上在运营首页界面中点击返回键 无反应的问题，该方法没有做任何的复写，所以不会有问题 FIXME 至于为什么了加了这个代码就没有问题了 没有找到原因 by zfxu FIXME
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Added in API level 5
     */
    @Override
    public void onBackPressed() {
        if (!mViewManager.onBackPressed(null)) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            data = new Intent();
        }
//        data.putExtra(RESULT_REQUEST, requestCode);
//        data.putExtra(RESULT_CODE, resultCode);
//        data.putExtra(RESUME_FORM, RESUME_FROM_RESULT);
//        mViewManager.resumeView(data);
    }

    @Override
    public void update(final IBaseView view, ViewUpdateType type) {

        final View updateView = view.getView();
//        updateView.setTag(view);
         View oldView = null;
        if (updateView != null) {
            if (mLayout.getChildAt(0) != null) {
                oldView = mLayout.getChildAt(0);
            }
            makeForceLayout(updateView);

            mLayout.addView(updateView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            switch (type)
            {
                case Enter:
                    if(view.getEnterAnimator() != null)
                    {
                        AnimatorsManager.AnimationComposer  animationComposer = AnimatorsManager.with(view.getEnterAnimator()).interpolate(new AccelerateInterpolator());
                        if(oldView != null)
                        {
                            animationComposer.withListener(enterAnimatorListener);
                        }
                        animationComposer.playOn(updateView);
                    }
                    else
                    {
                        if(oldView != null)
                        {
                            mLayout.removeViewAt(0);
                        }

                    }
                    break;
                case Exit:
                    if(oldView != null) {
                        if (view.getExitAnimator() != null) {
                            mLayout.removeViewAt(0);
                            mLayout.addView(oldView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT));
                            AnimatorsManager.with(view.getExitAnimator()).interpolate(new AccelerateInterpolator()).withListener(exitAnimatorListener).playOn(oldView);
                        }
                        else
                        {
                                mLayout.removeViewAt(0);
                        }
                    }
                    break;
                default:
                    if(oldView != null)
                    {
                        mLayout.removeViewAt(0);
                    }
                    break;
            }
//            if(oldView != null)
//            {
//
//                AnimatorSet mAnimatorSet = new AnimatorSet();
//                ViewGroup parent = (ViewGroup)updateView.getParent();
//                final int distance = parent.getWidth() - updateView.getLeft();
////
//                mAnimatorSet.addListener(enterAnimatorListener);
//                mAnimatorSet.setDuration(500).setInterpolator(new LinearInterpolator());
//                mAnimatorSet.playTogether(
//                        ObjectAnimator.ofFloat(updateView, "alpha", 0, 1),
//                        ObjectAnimator.ofFloat(updateView,"translationX",distance,0),
//                        ObjectAnimator.ofFloat(oldView, "alpha", 1, 0)
////                        ObjectAnimator.ofFloat(oldView,"translationX",0,-oldView.getRight())
//                );
//                mAnimatorSet.start();

//                ObjectAnimator anim = ObjectAnimator//
//                        .ofFloat(view, "zhy",   0.0F,1.0F)//
//                        .setDuration(500);//
//                anim.start();
//                final View finalOldView = oldView;
//                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
//                {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation)
//                    {
//                        float cVal = (Float) animation.getAnimatedValue();
//                        finalOldView.setTranslationX(-cVal*finalOldView.getRight());
//                        updateView.setTranslationX( (1-cVal)*distance);
//                    }
//                });
//                anim.addListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        mLayout.removeViewAt(0);
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//                        mLayout.removeViewAt(0);
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//                });
//            }


        }
    }



    /**
     * 解决某些手机上不会刷新的问题
     *
     * @param view
     */
    private void makeForceLayout(View view) {
        if (view instanceof ViewGroup) {
            int count = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < count; i++) {
                makeForceLayout(((ViewGroup) view).getChildAt(i));
            }
            view.forceLayout();
        } else {
            view.forceLayout();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mViewManager.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void finishView() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mViewManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 解决bug http://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this-action-after-
     * onsaveinstancestate-h
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // No call for super(). Bug on API Level > 11.
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
