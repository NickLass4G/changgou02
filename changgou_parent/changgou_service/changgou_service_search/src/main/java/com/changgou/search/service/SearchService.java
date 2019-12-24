package com.changgou.search.service;

import java.util.Map;

/**
 * @Author:Administrator
 * @Date: 2019/12/24 18:35
 */
public interface SearchService {

    /**
     * 全文检索
     * @param paramMap
     * @return
     * @throws Exception
     */
    Map search(Map<String,String> paramMap) throws Exception;
}
