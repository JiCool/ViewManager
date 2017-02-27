package com.example.mafei.viewmanager.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.animationlibrary.library.Animators;
import com.example.mafei.viewmanager.MainActivity;
import com.example.mafei.viewmanager.R;
import com.example.mafei.viewmanager.manager.ViewFlags;
import com.example.mafei.viewmanager.manager
        .interfaces.IViewManager;
import com.example.mafei.viewmanager.view.impl.BaseView;
import com.example.mafei.viewmanager.manager.MyIntent;
import com.example.mafei.viewmanager.manager.ViewLaunchMode;

/**
 * Created by jicool on 2017/2/6.
 */
public class View1 extends BaseView {
    public View1(Context context, IViewManager viewManager) {
        super(context, viewManager);
    }


    @Override
    public void createView(MyIntent intent) {
        super.createView(intent);
    }

    @Override
    public void resumeView() {
        super.resumeView();

        Button btn = (Button) mView.findViewById(R.id.btn1);
        btn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putLong("startTime", SystemClock.elapsedRealtime());
                MyIntent _MyIntent = mIntent;
                _MyIntent.setViewBundle(bundle);
                _MyIntent.setViewClass(View2.class);
                _MyIntent.setViewFlag(ViewFlags.FLAG_VIEW_NEW_TASK);
                startViewForResult(_MyIntent,123456);
            }
        });

        Button btn1 = (Button) mView.findViewById(R.id.btn2);
        btn1.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, MainActivity.class);
                mContext.startActivity(intent);
            }
        });

        Button btn2 = (Button) mView.findViewById(R.id.btn3);
        btn2.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, TestActivity2.class);
                mContext.startActivity(intent);
            }
        });

        long startTime = mIntent.getViewBundle()
                .getLong("startTime", 0);
        if (startTime != 0) {
            Toast.makeText(mContext, "耗时:" +
                    (SystemClock
                            .elapsedRealtime()
                            - startTime), Toast
                    .LENGTH_SHORT).show();
        }
    }

    @Override
    public View createView() {
        LayoutInflater inFlater = LayoutInflater.from(mContext);
        View view = inFlater.inflate(R.layout.activity_main, null);
        return view;
    }

    public ViewLaunchMode getLaunchMode()
    {
        return ViewLaunchMode.Standard;
    }

    @Override
    public void onViewResult(int requestCode, int resultCode, MyIntent intent) {
        super.onViewResult(requestCode, resultCode, intent);
        if(requestCode == 123456)
        {
            Toast.makeText(mContext,"result code is "+resultCode,Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public Animators getEnterAnimator() {
        return Animators.SlideInRight;
    }

    @Override
    public Animators getExitAnimator() {
        return Animators.SlideOutRight;
    }

}
