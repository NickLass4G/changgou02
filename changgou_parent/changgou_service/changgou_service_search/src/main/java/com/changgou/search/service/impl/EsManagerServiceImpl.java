package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.common.exception.ExceptionCast;
import com.changgou.common.model.response.search.SearchCode;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.ESManagerMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.EsManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author:Administrator
 * @Date: 2019/12/23 21:59
 */
@Service
public class EsManagerServiceImpl implements EsManagerService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private ESManagerMapper esManagerMapper;

    /**
     * 创建索引库
     */
    @Override
    public void createIndexAndMapping() {
        elasticsearchTemplate.createIndex(SkuInfo.class);
        elasticsearchTemplate.putMapping(SkuInfo.class);
    }

    /**
     * 将所有的sku数据导入ES
     */
    @Override
    public void importAll() {
        List<Sku> skuList = skuFeign.findSkuListBySpuId("all");
        if (skuList == null || skuList.size() <= 0){
            ExceptionCast.cast(SearchCode.SEARCH_SKU_EMPTY_ERROR);
        }
        // skuList转换为json
        String jsonSkuList = JSON.toJSONString(skuList);
        //将json转换为skuinfo
        List<SkuInfo> skuInfoList = JSON.parseArray(jsonSkuList, SkuInfo.class);

        for (SkuInfo skuInfo : skuInfoList) {
            // 将规格信息转化为map
            Map specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(specMap);
        }
        // 导入索引库
        esManagerMapper.saveAll(skuInfoList);
    }

    /**
     * 根据SpuId导入ES索引库
     * @param spuId
     */
    @Override
    public void importToESBySpuId(String spuId) {
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        if (skuList == null || skuList.size() <= 0){
            ExceptionCast.cast(SearchCode.SEARCH_SKU_EMPTY_ERROR);
        }
        // skuList转换为json
        String jsonSkuList = JSON.toJSONString(skuList);
        //将json转换为skuinfo
        List<SkuInfo> skuInfoList = JSON.parseArray(jsonSkuList, SkuInfo.class);

        for (SkuInfo skuInfo : skuInfoList) {
            // 将规格信息转化为map
            Map specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(specMap);
        }
        // 导入索引库
        esManagerMapper.saveAll(skuInfoList);
    }

    /**
     * 根据SpuId删除ES索引库中的数据
     * @param spuId
     */
    @Override
    public void deleteDataBySpuId(String spuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSpuId(Long.parseLong(spuId));
        esManagerMapper.delete(skuInfo);
    }
}
