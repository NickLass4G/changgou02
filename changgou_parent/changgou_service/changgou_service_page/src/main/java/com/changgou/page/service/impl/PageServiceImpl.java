package com.changgou.page.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.CategoryFeign;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.page.service.PageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:Administrator
 * @Date: 2019/12/27 21:14
 */
@Service
public class PageServiceImpl implements PageService{
    @Autowired
    private SpuFeign spuFeign;
    @Autowired
    private CategoryFeign categoryFeign;
    @Autowired
    private SkuFeign skuFeign;
    @Value("${pagepath}")
    private String pagePath;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 生成静态化页面
     * @param spuId
     */
    @Override
    public void createItemPage(String spuId) {

        // 获取context对象,用于存储静态化页面所需数据
        Context context = new Context();
        // .1 调用本类中的一个私有方法,查询所需数据
        Map<String,Object> itemData = this.findItemData(spuId);
        context.setVariables(itemData);
        // 获取生成页面后的存放位置
        File dir = new File(pagePath);
        // 判断存放位置的文件夹是否存在,不存在则创建
        if (!dir.exists()){
            dir.mkdirs();
        }
        // 定义输出流,进行文件生成
        File descFile = new File(dir+File.separator+spuId+".html");
        Writer out = null;

        try {
            out = new PrintWriter(descFile);
            // 生成文件
            /*
            * 1.模板名称
            * 2.context对象,包含生成模板所需的全部数据
            * 3.输出流,指定文件生成的位置
            *
            * */
            templateEngine.process("item",context,out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {// 关闭流
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询生成静态页面所需的全部数据
     * @param spuId
     * @return
     */
    private Map<String,Object> findItemData(String spuId) {
        Map<String,Object> resultMap = new HashMap<>();

        // 获取spu的信息
        Spu spu = spuFeign.findSpuById(spuId).getData();
        resultMap.put("spu",spu);
        // 获取图片的信息
        if (spu != null){
            if (StringUtils.isNotEmpty(spu.getImages())){
                resultMap.put("imageList",spu.getImages().split(","));
            }
        }

        // 获取商品分类的信息
        Category category1 = categoryFeign.findById(spu.getCategory1Id()).getData();
        System.out.println(categoryFeign.findById(spu.getCategory1Id()));
        // Result<Category> = categoryFeign.findById(spuId);
        Category category2 = categoryFeign.findById(spu.getCategory2Id()).getData();
        Category category3 = categoryFeign.findById(spu.getCategory3Id()).getData();
        resultMap.put("category1",category1);
        resultMap.put("category2",category2);
        resultMap.put("category3",category3);
        // 获取sku的信息
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        resultMap.put("skuList",skuList);

        // 获取商品规格信息
        resultMap.put("specificationList", JSON.parseObject(spu.getSpecItems(),Map.class));

        return resultMap;
    }
}
