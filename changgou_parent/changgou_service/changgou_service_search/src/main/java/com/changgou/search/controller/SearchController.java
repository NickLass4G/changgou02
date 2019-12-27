package com.changgou.search.controller;

import com.changgou.common.entity.Page;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.Set;

/**
 * @Author:Administrator
 * @Date: 2019/12/24 20:33
 */
@Controller
@RequestMapping("/sku_search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    // 搜索页面
    // 传参 Map  返回值也是Map
    // 由于页面是thymeleaf完成的,需要进行页面跳转
    @GetMapping("/list")
    public String search(@RequestParam Map<String,String> searchMap , Model model)throws Exception{
        // 特殊符号处理
        this.handlerParamMap(searchMap);

        // 执行查询返回值
        Map<String,Object> resultMap = searchService.search(searchMap);

        model.addAttribute("searchMap",searchMap);
        model.addAttribute("result",resultMap);

        //封装分页数据并返回
        //1.总记录数
        //2.当前页
        //3.每页显示多少条
        Page<SkuInfo> page = new Page<SkuInfo>(
                Long.parseLong(String.valueOf(resultMap.get("total"))),
                Integer.parseInt(String.valueOf(resultMap.get("pageNum"))),
                Page.pageSize
        );
        model.addAttribute("page",page);

        // 拼装url
        StringBuilder url = new StringBuilder("/sku_search/list");
        if (searchMap != null && searchMap.size() > 0){
            url.append("?");
            for (String paramKey : searchMap.keySet()) {
                if (!"sortRule".equals(paramKey)&&!"sortField".equals(paramKey)&&!"pageNum".equals(paramKey)){
                    url.append(paramKey).append("=").append(searchMap.get(paramKey)).append("&");
                }
            }
            String urlString = url.toString();
            // 去除最后一个 &
            urlString = urlString.substring(0,urlString.length()-1);
            model.addAttribute("url",urlString);
        }else {
            model.addAttribute("url",url.toString());
        }
        return "search";
    }

    /**
     * 全文检索
     * @param paramMap
     * @return
     * @throws Exception
     */
    @GetMapping
    @ResponseBody
    public Map search(@RequestParam Map<String , String> paramMap)throws Exception{
        // 处理特殊符号
        handlerParamMap(paramMap);
        Map search = searchService.search(paramMap);
        return search;
    }

    /**
     * 处理特殊符号的方法
     * @param paramMap
     */
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
