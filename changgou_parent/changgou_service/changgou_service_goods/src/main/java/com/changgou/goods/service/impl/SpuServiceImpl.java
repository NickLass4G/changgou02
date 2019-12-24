package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.common.exception.ExceptionCast;
import com.changgou.common.model.response.goods.GoodsCode;
import com.changgou.common.util.IdWorker;
import com.changgou.goods.dao.*;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    /**
     * 查询全部列表
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @Override
    public Goods findById(String id){
        Goods goods = new Goods();
        Spu spu = spuMapper.selectByPrimaryKey(id);
        goods.setSpu(spu);
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", spu.getId());
        List<Sku> skuList = skuMapper.selectByExample(example);
        goods.setSkuList(skuList);
        return goods;
    }


    /**
     * 增加
     * @param goods
     */
    @Override
    @Transactional
    public void add(Goods goods){
        // 向数据库中存入Spu对象
        Spu spu = goods.getSpu();
        if (spu == null){
            ExceptionCast.cast(GoodsCode.GOODS_SPU_ADD_ERROR);
        }
        // 生成id
        long id = idWorker.nextId();
        spu.setId(String.valueOf(id));
        // 设置三种状态
        spu.setIsDelete("0");
        spu.setIsMarketable("0");
        spu.setStatus("0");
        spuMapper.insertSelective(spu);

        // 将List<Sku>集合保存到数据库
        saveSkuList(goods);
    }




    /**
     * 修改
     * @param goods
     */
    @Override
    public void update(Goods goods){
        Spu spu = goods.getSpu();
        spuMapper.updateByPrimaryKey(spu);

        // 删除原先sku表中的数据
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",spu.getId());
        skuMapper.deleteByExample(example);

        // 再插入新的skuList数据
        saveSkuList(goods);
    }

    /**
     * 删除,逻辑删除
     * @param id
     */
    @Override
    public void delete(String id){
        Spu spu = spuMapper.selectByPrimaryKey(id);
        // 是否正在上架
        if ("1".equals(spu.getIsMarketable())){
            ExceptionCast.cast(GoodsCode.GOODS_SPU_DELETE_ERROR);
        }
        spu.setIsDelete("1");
        spu.setStatus("0");
        // 保存修改
        spuMapper.updateByPrimaryKeySelective(spu);
    }


    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    @Override
    public List<Spu> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Spu> findPage(int page, int size){
        PageHelper.startPage(page,size);
        return (Page<Spu>)spuMapper.selectAll();
    }

    /**
     * 条件+分页查询
     * @param searchMap 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public Page<Spu> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Spu>)spuMapper.selectByExample(example);
    }

    /**
     * 审核
     * @param id
     */
    @Override
    public void examine(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null){
            // 商品不存在异常
            ExceptionCast.cast(GoodsCode.GOODS_SPU_NOT_FOUND_ERROR);
        }
        // 是否处于逻辑删除状态
        if ("1".equals(spu.getIsDelete())){
            ExceptionCast.cast(GoodsCode.GOODS_SPU_IS_DELETE_ERROR);
        }
        // 修改审核状态为"1",并自动上架,修改状态为"1"
        spu.setStatus("1");
        spu.setIsMarketable("1");

        // 保存修改
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 商品下架
     * @param id
     */
    @Override
    public void pull(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null){
            // 商品不存在异常
            ExceptionCast.cast(GoodsCode.GOODS_SPU_NOT_FOUND_ERROR);
        }
        // 是否处于逻辑删除状态
        if ("1".equals(spu.getIsDelete())){
            ExceptionCast.cast(GoodsCode.GOODS_SPU_IS_DELETE_ERROR);
        }
        // 是否处于已下架
        if ("0".equals(spu.getIsMarketable())){
            ExceptionCast.cast(GoodsCode.GOODS_SPU_PULL_REPEAT_ERROR);
        }
        // 修改上架状态为 "0"
        spu.setIsMarketable("0");

        // 保存修改
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 商品上架
     * @param id
     */
    @Override
    public void putOn(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null){
            // 商品不存在异常
            ExceptionCast.cast(GoodsCode.GOODS_SPU_NOT_FOUND_ERROR);
        }
        // 是否处于逻辑删除状态
        if ("1".equals(spu.getIsDelete())){
            ExceptionCast.cast(GoodsCode.GOODS_SPU_IS_DELETE_ERROR);
        }
        // 是否审核通过
        if ("0".equals(spu.getStatus())){
            ExceptionCast.cast(GoodsCode.GOODS_SPU_IS_EXAMINE_ERROR);
        }
        // 修改上架状态为 "1"
        spu.setIsMarketable("1");

        // 保存修改
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 商品还原
     * @param id
     */
    @Override
    public void restore(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        // 是否被逻辑删除
        if ("0".equals(spu.getIsDelete())){
            ExceptionCast.cast(GoodsCode.GOODS_SPU_RESTORE_ERROR);
        }
        spu.setStatus("0");
        spu.setIsDelete("0");

        // 保存修改
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 物理删除商品
     * @param id
     */
    @Override
    public void realDelete(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        // 是否被逻辑删除
        if ("0".equals(spu.getIsDelete())){
            ExceptionCast.cast(GoodsCode.GOODS_SPU_RESTORE_ERROR);
        }
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 构建查询对象
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 主键
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andEqualTo("id",searchMap.get("id"));
           	}
            // 货号
            if(searchMap.get("sn")!=null && !"".equals(searchMap.get("sn"))){
                criteria.andEqualTo("sn",searchMap.get("sn"));
           	}
            // SPU名
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
           	}
            // 副标题
            if(searchMap.get("caption")!=null && !"".equals(searchMap.get("caption"))){
                criteria.andLike("caption","%"+searchMap.get("caption")+"%");
           	}
            // 图片
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
           	}
            // 图片列表
            if(searchMap.get("images")!=null && !"".equals(searchMap.get("images"))){
                criteria.andLike("images","%"+searchMap.get("images")+"%");
           	}
            // 售后服务
            if(searchMap.get("saleService")!=null && !"".equals(searchMap.get("saleService"))){
                criteria.andLike("saleService","%"+searchMap.get("saleService")+"%");
           	}
            // 介绍
            if(searchMap.get("introduction")!=null && !"".equals(searchMap.get("introduction"))){
                criteria.andLike("introduction","%"+searchMap.get("introduction")+"%");
           	}
            // 规格列表
            if(searchMap.get("specItems")!=null && !"".equals(searchMap.get("specItems"))){
                criteria.andLike("specItems","%"+searchMap.get("specItems")+"%");
           	}
            // 参数列表
            if(searchMap.get("paraItems")!=null && !"".equals(searchMap.get("paraItems"))){
                criteria.andLike("paraItems","%"+searchMap.get("paraItems")+"%");
           	}
            // 是否上架
            if(searchMap.get("isMarketable")!=null && !"".equals(searchMap.get("isMarketable"))){
                criteria.andEqualTo("isMarketable",searchMap.get("isMarketable"));
           	}
            // 是否启用规格
            if(searchMap.get("isEnableSpec")!=null && !"".equals(searchMap.get("isEnableSpec"))){
                criteria.andEqualTo("isEnableSpec", searchMap.get("isEnableSpec"));
           	}
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andEqualTo("isDelete",searchMap.get("isDelete"));
           	}
            // 审核状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status",searchMap.get("status"));
           	}

            // 品牌ID
            if(searchMap.get("brandId")!=null ){
                criteria.andEqualTo("brandId",searchMap.get("brandId"));
            }
            // 一级分类
            if(searchMap.get("category1Id")!=null ){
                criteria.andEqualTo("category1Id",searchMap.get("category1Id"));
            }
            // 二级分类
            if(searchMap.get("category2Id")!=null ){
                criteria.andEqualTo("category2Id",searchMap.get("category2Id"));
            }
            // 三级分类
            if(searchMap.get("category3Id")!=null ){
                criteria.andEqualTo("category3Id",searchMap.get("category3Id"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }
            // 运费模板id
            if(searchMap.get("freightId")!=null ){
                criteria.andEqualTo("freightId",searchMap.get("freightId"));
            }
            // 销量
            if(searchMap.get("saleNum")!=null ){
                criteria.andEqualTo("saleNum",searchMap.get("saleNum"));
            }
            // 评论数
            if(searchMap.get("commentNum")!=null ){
                criteria.andEqualTo("commentNum",searchMap.get("commentNum"));
            }

        }
        return example;
    }

    /**
     * 将skuList集合保存到数据库
     * @param goods
     */
    private void saveSkuList(Goods goods) {
        // 标准产品单位
        Spu spu = goods.getSpu();
        // 当前时间
        Date date = new Date();
        // 品牌对象
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
        // 分类对象
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        List<Sku> skuList = goods.getSkuList();
        if (skuList == null || skuList.size() <= 0){
            ExceptionCast.cast(GoodsCode.GOODS_SKU_ADD_ERROR);
        }

        /*添加分类与品牌关联表中的信息*/
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setBrandId(brand.getId());
        categoryBrand.setCategoryId(category.getId());
        int count = categoryBrandMapper.selectCount(categoryBrand);
        if (count == 0){
            // 数据库中没有这条信息
            categoryBrandMapper.insert(categoryBrand);
        }


        // 库存量不为空,可以添加
        for (Sku sku : skuList) {
            // Sku skuForDatabase = new Sku();
            // id
            sku.setId(String.valueOf(idWorker.nextId()));
            // 规格spec
            if (sku.getSpec() == null || "".equals(sku.getSpec())){
                sku.setSpec("{}");
            }
            // name = 规格+商品名称
            String name = spu.getName();
            // 将规格的json转换成map获取值
            Map<String,String> map = JSON.parseObject(sku.getSpec(), Map.class);
            if (map != null && map.size() > 0){
                for (String value : map.values()) {
                    name += " "+value;
                }
            }
            sku.setName(name);
            sku.setCreateTime(date);
            sku.setUpdateTime(date);
            sku.setSpuId(spu.getId());
            sku.setCategoryId(category.getId());
            sku.setCategoryName(category.getName());
            sku.setBrandName(brand.getName());
            skuMapper.insertSelective(sku);
        }
    }

}
