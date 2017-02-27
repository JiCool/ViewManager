package com.example.mafei.viewmanager;

import android.os.Bundle;
import android.widget.Toast;

import com.example.mafei.viewmanager.manager
        .ViewFlags;
import com.example.mafei.viewmanager.manager.activitys.ViewActivity;
import com.example.mafei.viewmanager.view.navigator.test.TabHostTest;
import com.example.mafei.viewmanager.manager.MyIntent;

public class MainActivity extends ViewActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyIntent intent = new MyIntent();
//        intent.setClass(this, TestActivity2.class);
        intent.setViewClass(TabHostTest.class);
//        intent.setViewClass(ScrollTabHostTest1.class);
        intent.setViewFlag(ViewFlags.FLAG_VIEW_NEW_TASK);
        Toast.makeText(this,"this is "+getLocalClassName(),Toast.LENGTH_SHORT).show();
       startViewActivity(intent);
    }
}
