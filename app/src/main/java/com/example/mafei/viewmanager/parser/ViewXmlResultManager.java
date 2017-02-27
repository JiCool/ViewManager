package com.example.mafei.viewmanager.parser;

import android.content.Context;
import android.util.Log;

import com.example.mafei.viewmanager.parser
        .entity.ViewEntity;

import java.io.InputStream;
import java.util.List;

/**
 * Created by jicool on 2017/1/12.
 */
public class ViewXmlResultManager {
   private static List<ViewEntity> views;
    private static String TAG = "ViewXmlResultManager";
    public static  List<ViewEntity>  getConfigViews(Context mContext)
    {

            if(views == null || views.size() <=0)
            {
                try {
                InputStream is = mContext.getAssets().open("view_config.xml");
//          parser = new SaxBookParser();
//          parser = new DomBookParser();
                PullViewParser parser = new PullViewParser();
                views = parser.parse(is);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            }
          return views;
    }
}
