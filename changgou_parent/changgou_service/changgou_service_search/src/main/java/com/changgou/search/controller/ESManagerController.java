package com.changgou.search.controller;

import com.changgou.common.entity.Result;
import com.changgou.common.entity.StatusCode;
import com.changgou.search.service.EsManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author:Administrator
 * @Date: 2019/12/23 22:41
 */
@RestController
@RequestMapping("/manager")
public class ESManagerController {
    @Autowired
    private EsManagerService esManagerService;


    @GetMapping("/create")
    public Result create(){
        esManagerService.createIndexAndMapping();
        return new Result(true, StatusCode.OK,"索引库创建成功");
    }

    @GetMapping("/importAll")
    public Result importAll(){
        esManagerService.importAll();
        return new Result(true,StatusCode.OK,"导入全部数据成功");
    }

    @DeleteMapping("/{spuId}")
    public Result delete(@PathVariable("spuId")String spuId){
        esManagerService.deleteDataBySpuId(spuId);
        return new Result(true,StatusCode.OK,"根据SpuId删除删除ES中数据成功");
    }
}
