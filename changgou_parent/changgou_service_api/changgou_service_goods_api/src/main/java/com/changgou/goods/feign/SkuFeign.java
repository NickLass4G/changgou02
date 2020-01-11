package com.changgou.goods.feign;

import com.changgou.common.entity.Result;
import com.changgou.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author:Administrator
 * @Date: 2019/12/23 21:34
 */
@FeignClient(name = "goods")
//@RequestMapping("/sku")

public interface SkuFeign {
    @GetMapping("/sku/spu/{spuId}")
    public List<Sku> findSkuListBySpuId(@PathVariable("spuId") String spuId);

    @PostMapping("/sku/count")
    String findTotalCount(@RequestBody Map<String,String> map);

    @PostMapping("/sku/search/{page}/{size}")
    public Result findPage(@RequestBody Map searchMap, @PathVariable  int page, @PathVariable  int size);

    @GetMapping("/sku/{id}")
    public Result<Sku> findById(@PathVariable String id);

    @PostMapping("/sku/decr/count")
    public Result decrCount(@RequestParam("username") String username);

}
