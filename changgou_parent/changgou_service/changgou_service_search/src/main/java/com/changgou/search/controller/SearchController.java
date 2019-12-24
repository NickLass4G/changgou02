package com.changgou.search.controller;

import com.changgou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

/**
 * @Author:Administrator
 * @Date: 2019/12/24 20:33
 */
@RestController
@RequestMapping("/sku_search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    /**
     * 全文检索
     * @param paramMap
     * @return
     * @throws Exception
     */
    @GetMapping
    public Map search(@RequestParam Map<String , String> paramMap)throws Exception{
        // 处理特殊符号
        handlerParamMap(paramMap);
        Map search = searchService.search(paramMap);
        return search;
    }

    private void handlerParamMap(Map<String, String> paramMap) {
        if (paramMap != null){
            Set<Map.Entry<String, String>> entrySet = paramMap.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                if (entry.getKey().startsWith("spec_")){
                    paramMap.put(entry.getKey(),entry.getValue().replace("+","%2B"));
                }
            }
        }
    }
}
