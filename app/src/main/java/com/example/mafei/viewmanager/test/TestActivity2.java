package com.example.mafei.viewmanager.test;

import android.os.Bundle;
import android.widget.Toast;

import com.example.mafei.viewmanager.manager.activitys.ViewActivity;
import com.example.mafei.viewmanager.manager.ViewFlags;
import com.example.mafei.viewmanager.manager.MyIntent;

public class TestActivity2 extends ViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyIntent intent = new MyIntent();
//        intent.setClass(this, TestActivity2.class);
        intent.setViewClass(View1.class);
        intent.setViewFlag(ViewFlags.FLAG_VIEW_NEW_TASK);
        Toast.makeText(this,"this is "+getLocalClassName(),Toast.LENGTH_SHORT).show();
//        Debug.waitForDebugger();
        startViewActivity(intent);
    }
}
