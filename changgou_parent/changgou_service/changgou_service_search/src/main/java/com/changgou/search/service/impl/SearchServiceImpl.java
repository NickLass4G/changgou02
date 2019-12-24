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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Map search(Map<String, String> paramMap) throws Exception {
        Map<String,Object> resultMap = new HashMap<>();

        // 构建查询对象
        if (paramMap != null){
            // 构建查询条件封装对象
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            // 按照关键字查询
            if (StringUtils.isNotEmpty(paramMap.get("keywords"))){
                boolQuery.must(QueryBuilders.matchQuery("name",paramMap.get("keywords")).operator(Operator.AND));
            }

            // 按照品牌查询
            if (StringUtils.isNotEmpty(paramMap.get("brand"))){
                boolQuery.filter(QueryBuilders.termQuery("brandName",paramMap.get("brand")));
            }

            // 按照规格进行过滤查询
            for (String key : paramMap.keySet()) {
                if (key.startsWith("spec_")){
                    String value = paramMap.get(key).replace("%2B", "+");
                    //
                    boolQuery.filter(QueryBuilders.termQuery(("spec."+key.substring(5)+".keyword"),value));
                }
            }

            //按照价格区间进行过滤查询
            if (StringUtils.isNotEmpty(paramMap.get("price"))){
                String[] prices = paramMap.get("price").split("-");
                // 500-100
                if (prices.length == 2){
                    boolQuery.filter(QueryBuilders.rangeQuery("price").lte(prices[1]));
                }
                boolQuery.filter(QueryBuilders.rangeQuery("price").gte(prices[0]));
            }
            nativeSearchQueryBuilder.withQuery(boolQuery);

            //按照品牌进行分组(聚合)查询
            String skuBrand = "skuBrand";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuBrand).field("brandName"));

            //按照规格进行聚合查询
            String skuSpec = "skuSpec";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuSpec).field("spec.keyword"));

            //开启分页查询
            // 当前页
            String currentPage = paramMap.get("currentPage");
            // 每页显示的条数
            String pageSize = paramMap.get("pageSize");
            if (StringUtils.isEmpty(currentPage)){
                currentPage = "1";
            }
            if (StringUtils.isEmpty(pageSize)){
                pageSize = "30";
            }
            //设置分页
            //第一个参数:当前页 是从0开始
            //第二个参数:每页显示多少条
            nativeSearchQueryBuilder.withPageable(PageRequest.of(Integer.parseInt(currentPage)-1, Integer.parseInt(pageSize)));

            // 按照相关字段进行排序查询
            // 1.当前域,2.当前的排序操作()ASC DESC
            if (StringUtils.isNotEmpty(paramMap.get("sortField")) && StringUtils.isNotEmpty(paramMap.get("sortRule"))){
                if ("ASC".equals(paramMap.get("sortRule"))){
                    // 升序
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(paramMap.get("sortRule")).order(SortOrder.ASC));
                }else {
                    // 降序
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(paramMap.get("sortRule")).order(SortOrder.DESC));
                }
            }

            //设置高亮域以及高亮的样式
            HighlightBuilder.Field field = new  HighlightBuilder.Field("name") // 高亮域
                    .preTags("<span style =  'color:red'>")
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

                    // 获取查询命中结果
                    SearchHits hits = searchResponse.getHits();
                    if (hits != null){
                        // 查询结果有数据
                        for (SearchHit hit : hits) {
                            //SearchHit转换为skuinfo
                            SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);

                            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                            if (highlightFields != null && highlightFields.size() > 0){
                                //替换数据
                                skuInfo.setName(highlightFields.get("name").getFragments()[0].toString());
                            }
                            list.add((T) skuInfo);
                        }
                    }
                    return new AggregatedPageImpl<T>(list,pageable,hits.getTotalHits(),searchResponse.getAggregations());
                }
            });

            //封装最终的返回结果
            //  总记录数
            resultMap.put("total",resultInfo.getTotalElements());
            //总页数
            resultMap.put("totalPages",resultInfo.getTotalPages());
            //数据集合
            resultMap.put("rows",resultInfo.getContent());
            //封装品牌的分组结果
            StringTerms brandTerms = (StringTerms) resultInfo.getAggregation(skuBrand);
            List<String> brandList = brandTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            resultMap.put("brandList",brandList);
            //封装规格分组结果
            StringTerms specTerms = (StringTerms) resultInfo.getAggregation(skuSpec);
            List<String> specList = specTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            resultMap.put("specList",specList);
            //当前页
            resultMap.put("currentPage",currentPage);
            return resultMap;
        }
        return null;
    }
}
