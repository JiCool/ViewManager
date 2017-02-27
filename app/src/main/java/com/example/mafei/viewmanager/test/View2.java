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

/**
 * Created by jicool on 2017/2/6.
 */
public class View2 extends BaseView{
    public View2(Context context, IViewManager viewManager) {
        super(context,viewManager);
    }

    @Override
    public void resumeView() {
        super.resumeView();
        Button btn = (Button) mView.findViewById(R.id.btn2);
        btn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putLong("startTime",
                        SystemClock.elapsedRealtime());
                MyIntent _MyIntent = mIntent;
                mIntent.setViewFlag(ViewFlags.FLAG_VIEW_NEW_TASK);
                _MyIntent.setViewBundle(bundle);
                _MyIntent.setViewClass(View1.class);
//                setResult(654321,_MyIntent);
                startView(_MyIntent);
            }
        });

        Button btn1 = (Button) mView.findViewById(R.id.btn3);
        btn1.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, MainActivity.class);
                mContext.startActivity(intent);
            }
        });

        Button btn2 = (Button) mView.findViewById(R.id.btn4);
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
        View view = inFlater.inflate(R.layout.view2, null);
        return view;
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
