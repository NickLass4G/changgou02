package com.changgou.goods.service.impl;

import com.changgou.common.util.PinYinUtils;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 品牌列表查询
     * @return
     */
    @Override
    public List<Brand> findAll() {
//        int i = 1 / 0;
        List<Brand> brandList = brandMapper.selectAll();
        return brandList;
    }

    /**
     * 根据id查询品牌信息
     * @param id
     * @return
     */
    @Override
    public Brand findById(Integer id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        return brand;
    }

    /**
     * 品牌新增
     * @param brand
     */
    @Override
    @Transactional
    public void add(Brand brand) {

        String name = brand.getName();
        if (StringUtils.isBlank(name)) {
            throw new RuntimeException("非法参数");
        }

        // 1 判断 填写 品牌首字母
        String letter = brand.getLetter();
        if (StringUtils.isBlank(letter)) {  // 没传参数
            letter = PinYinUtils.getFirstLetter(name);  //  name ? null
        } else {
            // 转成大小存储到数据库
           letter = letter.toUpperCase();
        }
        brand.setLetter(letter);

        brandMapper.insertSelective(brand);
    }

    /**
     * 品牌修改
     * @param brand
     */
    @Override
    @Transactional
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    /**
     * 根据id删除品牌信息
     * @param id
     */
    @Override
    @Transactional
    public void delete(Integer id) {
        brandMapper.deleteByPrimaryKey(id);
    }

    /**
     * 品牌列表条件查询
     * @param searchMap
     * @return
     */
    @Override
    public List<Brand> findList(Map<String, Object> searchMap) {
        Example example = new Example(Brand.class);
        //封装查询条件
        Example.Criteria criteria = example.createCriteria();
        if (searchMap!=null){
            //品牌名称(模糊) like  %
            if (searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            //按照品牌首字母进行查询(精确)
            if (searchMap.get("letter")!=null && !"".equals(searchMap.get("letter"))){
                criteria.andEqualTo("letter",searchMap.get("letter"));
            }
        }
        List<Brand> brandList = brandMapper.selectByExample(example);

        return brandList;
    }

    @Override
    public Page<Brand> findPage(Integer page, Integer size) {

        // 参数校验
        if (page == null || page <=0) {
            // 1 默认值
            page = 1;
            // 2 抛异常 必填参数  参数非法
        }
        if (size == null || size <= 0) {
            size = 10;
        }

        // 业务处理  分页插件 只对当前查询有效
        PageHelper.startPage(page,size);
        Page<Brand> page1 = (Page<Brand>) brandMapper.selectAll();

        return page1;
    }

    @Override
    public Page<Brand> findPage(Map<String, Object> searchMap, Integer page, Integer size) {

        // 参数校验
        if (page == null || page <=0) {
            // 1 默认值
            page = 1;
            // 2 抛异常 必填参数  参数非法
        }
        if (size == null || size <= 0) {
            size = 10;
        }
        //设置分页
        PageHelper.startPage(page,size);

        //设置查询条件
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap!=null){
            //设置品牌名称模糊查询
            if (searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            //设置品牌首字母的精确查询
            if (searchMap.get("letter")!=null && !"".equals(searchMap.get("letter"))){
                criteria.andEqualTo("letter",searchMap.get("letter"));
            }
        }
        Page<Brand> pageInfo = (Page<Brand>) brandMapper.selectByExample(example);
        return pageInfo;
    }

    /**
     * 根据分类名称查询品牌名称和img
     * @param categoryName
     * @return
     */
    @Override
    public List<Map> findListByCategoryName(String categoryName) {
        return brandMapper.findListByCategoryName(categoryName);
    }
}
