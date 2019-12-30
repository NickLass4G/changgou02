package com.changgou.goods.feign;

import com.changgou.common.entity.Result;
import com.changgou.goods.pojo.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author:Administrator
 * @Date: 2019/12/27 21:07
 */
@FeignClient(name = "goods")
public interface CategoryFeign {

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/category/{id}")
    public Result<Category> findById(@PathVariable("id") Integer id);
}
