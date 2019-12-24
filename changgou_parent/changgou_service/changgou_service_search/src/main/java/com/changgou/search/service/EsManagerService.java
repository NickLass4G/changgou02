package com.changgou.search.service;

/**
 * @Author:Administrator
 * @Date: 2019/12/23 21:53
 */
public interface EsManagerService {

    /**
     * 创建索引库
     */
    void createIndexAndMapping();

    /**
     * 将sku全部数据导入到索引库
     */
    void importAll();

    /**
     * 根据spuId查询到数据并新增到ES
     * @param spuId
     */
    void importToESBySpuId(String spuId);

    /**
     * 根据SpuId删除ES中的信息
     * @param spuId
     */
    void deleteDataBySpuId(String spuId);
}
