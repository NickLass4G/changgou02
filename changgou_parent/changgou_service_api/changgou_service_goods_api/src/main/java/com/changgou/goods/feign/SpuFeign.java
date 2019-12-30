package com.changgou.goods.feign;

import com.changgou.common.entity.Result;
import com.changgou.goods.pojo.Spu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author:Administrator
 * @Date: 2019/12/27 21:12
 */
@FeignClient(name = "goods")
public interface SpuFeign {
    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/spu/findSpuById/{id}")
    public Result<Spu> findSpuById(@PathVariable String id);
}
