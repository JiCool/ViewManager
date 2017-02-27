package com.example.mafei.viewmanager.parser.interfaces;


import com.example.mafei.viewmanager.parser
        .entity.ViewEntity;

import java.io.InputStream;
import java.util.List;

/**
 * Created by jicool on 2017/1/12.
 */
public interface ViewParser {
    /**
     * 解析输入流 得到Book对象集合
     * @param is
     * @return
     * @throws Exception
     */
    public List<ViewEntity> parse(InputStream is) throws Exception;

    /**
     * 序列化Book对象集合 得到XML形式的字符串
     * @param books
     * @return
     * @throws Exception
     */
    public String serialize(List<ViewEntity> books) throws Exception;
}
