package com.example.mafei.viewmanager.parser;

import android.util.Xml;

import com.example.mafei.viewmanager.parser
        .entity.ViewEntity;
import com.example.mafei.viewmanager.parser
        .interfaces.ViewParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jicool on 2017/1/12.
 */
public class PullViewParser implements ViewParser {
    @Override
    public List<ViewEntity> parse(InputStream is) throws Exception {
        List<ViewEntity> views = null;
        ViewEntity view = null;

//      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();  
//      XmlPullParser parser = factory.newPullParser();  
        
        XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例
        parser.setInput(is, "UTF-8");               //设置输入流 并指明编码方式  
        
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    views = new ArrayList<ViewEntity>();
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("view")) {
                        view = new ViewEntity();
                    } else if (parser.getName().equals("name")) {
                        eventType = parser.next();
                        view.setmClassName(parser.getText());
                    } else if (parser.getName().equals("flag")) {
                        eventType = parser.next();
                        view.setmFlag(parser.getText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("view")) {
                        views.add(view);
                        view = null;
                    }
                    break;
            }
            eventType = parser.next();
        }
        return views;
    }
    
    @Override
    public String serialize(List<ViewEntity> views) throws Exception {
//      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();  
//      XmlSerializer serializer = factory.newSerializer();  
        
        XmlSerializer serializer = Xml.newSerializer(); //由android.util.Xml创建一个XmlSerializer实例
        StringWriter writer = new StringWriter();
        serializer.setOutput(writer);   //设置输出方向为writer  
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "views");
        for (ViewEntity view : views) {
            serializer.startTag("", "view");
//            serializer.attribute("", "name", book.getId() + "");
            
            serializer.startTag("", "name");
            serializer.text(view.getmClassName());
            serializer.endTag("", "name");
            
            serializer.startTag("", "flag");
            serializer.text(view.getmFlag());
            serializer.endTag("", "flag");
        }
        serializer.endTag("", "views");
        serializer.endDocument();
        
        return writer.toString();
    }
}
