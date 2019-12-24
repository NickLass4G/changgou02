package com.changgou.search.dao;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:Administrator
 * @Date: 2019/12/23 21:52
 */
public interface ESManagerMapper extends ElasticsearchRepository<SkuInfo,Long> {
}
