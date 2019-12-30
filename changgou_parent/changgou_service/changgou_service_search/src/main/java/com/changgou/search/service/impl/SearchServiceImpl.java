package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author:Administrator
 * @Date: 2019/12/24 18:37
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public Map search(Map<String, String> searchMap) throws Exception {
        Map<String,Object> resultMap = new HashMap<>();

        // 构建查询
        if (searchMap != null){
            // 构建查询条件封装对象
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            // 按照关键字查询
            if (StringUtils.isNotEmpty(searchMap.get("keywords"))){
                boolQuery.must(QueryBuilders.matchQuery("name",searchMap.get("keywords")).operator(Operator.AND));
            }

            // 按照品牌进行过滤查询
            if (StringUtils.isNotEmpty(searchMap.get("brand"))){
                boolQuery.filter(QueryBuilders.termQuery("brandName",searchMap.get("brand")));
            }

            // 按照规格进行过滤查询
            for (String key : searchMap.keySet()) {
                if (key.startsWith("spec_")){
                    String value = searchMap.get(key).replace("%2B", "+");
                    boolQuery.filter(QueryBuilders.termQuery("specMap."+key.substring(5)+".keyword",value));
                }
            }

            // 按照价格区间进行过滤查询
            if (StringUtils.isNotEmpty(searchMap.get("price"))){
                String[] prices = searchMap.get("price").split("-");
                if (prices.length == 2){
                    boolQuery.filter(QueryBuilders.rangeQuery("price").lte(prices[1]));
                }
                boolQuery.filter(QueryBuilders.rangeQuery("price").gte(prices[0]));
            }

            // 我感觉bug在这里
                nativeSearchQueryBuilder.withQuery(boolQuery);

            // 按照品牌进行分组(聚合)查询
            String skuBrand = "skuBrand";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuBrand).field("brandName"));

            // 按照规格进行分组查询
            String skuSpec = "skuSpec";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuSpec).field("spec.keyword"));

            // 开启分页查询
            String currentPage = searchMap.get("pageNum"); //当前页
            String pageSize = searchMap.get("pageSize"); // 每页显示的条数
            if (StringUtils.isEmpty(currentPage)){
                currentPage = "1";
            }
            if (StringUtils.isEmpty(pageSize)){
                pageSize = "30";
            }

            //设置分页
            //第一个参数:当前页 是从0开始
            //第二个参数:每页显示多少条
            nativeSearchQueryBuilder.withPageable(PageRequest.of(Integer.valueOf(currentPage)-1,Integer.valueOf(pageSize)));

            // 按照相关字段进行排序查询
            // 1.当前域 2.当前的排序规则ASC DESC
            if (StringUtils.isNotEmpty(searchMap.get("sortField")) && StringUtils.isNotEmpty(searchMap.get("sortRule"))){
                if ("ASC".equals(searchMap.get("sortRule"))){
                    // 升序
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(searchMap.get("sortField")).order(SortOrder.ASC));
                }else {
                    // 降序
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(searchMap.get("sortField")).order(SortOrder.DESC));
                }
            }

            // 设置高亮区域以及高亮样式
            HighlightBuilder.Field field = new HighlightBuilder.Field("name")
                    .preTags("<span style='color:red'>")
                    .postTags("</span>");
            nativeSearchQueryBuilder.withHighlightFields(field);
            //开启查询
            /**
             * 第一个参数: 条件构建对象
             * 第二个参数: 查询操作实体类
             * 第三个参数: 查询结果操作对象
             */
            //封装查询结果
            AggregatedPage<SkuInfo> resultInfo = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                    // 查询结果操作
                    List<T> list = new ArrayList<>();

                    // 获取查询命中结果数据
                    SearchHits hits = searchResponse.getHits();
                    if (hits != null) {
                        // 有结果
                        for (SearchHit hit : hits) {
                            // SearchHit转换为skuInfo
                            SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);

                            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                            if (highlightFields != null && highlightFields.size() > 0) {
                                skuInfo.setName(highlightFields.get("name").getFragments()[0].toString());
                            }
                            list.add((T) skuInfo);
                        }
                    }
                    return new AggregatedPageImpl<>(list, pageable, hits.getTotalHits(), searchResponse.getAggregations());
                }
            });

            // 封装最终的返回结果
            // 总记录数
            resultMap.put("total",resultInfo.getTotalElements());
            // 总页数
            resultMap.put("totalPages",resultInfo.getTotalPages());
            // 数据集合
            resultMap.put("rows",resultInfo.getContent());
            // 封装品牌的分组结果
            StringTerms brandTerms = (StringTerms) resultInfo.getAggregation(skuBrand);
            List<String> brandList = brandTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            resultMap.put("brandList",brandList);
            // 封装规格分组结果
            StringTerms specTerms = (StringTerms) resultInfo.getAggregation(skuSpec);
            List<String> specList = specTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            resultMap.put("specList",this.formartSpec(specList));
            // 当前页
            resultMap.put("pageNum",currentPage);
            return resultMap;
        }
        return null;
    }

    /**
     * 原有数据
     *  [
     *         "{'颜色': '黑色', '尺码': '平光防蓝光-无度数电脑手机护目镜'}",
     *         "{'颜色': '红色', '尺码': '150度'}",
     *         "{'颜色': '黑色', '尺码': '150度'}",
     *         "{'颜色': '黑色'}",
     *         "{'颜色': '红色', '尺码': '100度'}",
     *         "{'颜色': '红色', '尺码': '250度'}",
     *         "{'颜色': '红色', '尺码': '350度'}",
     *         "{'颜色': '黑色', '尺码': '200度'}",
     *         "{'颜色': '黑色', '尺码': '250度'}"
     *     ]
     *
     *    需要的数据格式
     *    {
     *        颜色:[黑色,红色],
     *        尺码:[100度,150度]
     *    }
     */
    public Map<String, Set<String>> formartSpec(List<String> speList){
        Map<String, Set<String>> resultMap = new HashMap<>();
        if (speList != null && speList.size()>0){
            for (String specJsonString : speList) {
                // 将json转化为Map
                Map<String,String> specMap = JSON.parseObject(specJsonString, Map.class);
                for (String specKey : specMap.keySet()) {
                    Set<String> specSet = resultMap.get(specKey);
                    if (specSet == null){
                        specSet = new HashSet<String>();
                    }
                    // 将规格的值放入set集合
                    specSet.add(specMap.get(specKey));
                    // 将set集合放入到map集合
                    resultMap.put(specKey,specSet);
                    Map<String,String> hashMap = new HashMap<>(13334);
                }
            }
        }
        return resultMap;
    }
}
