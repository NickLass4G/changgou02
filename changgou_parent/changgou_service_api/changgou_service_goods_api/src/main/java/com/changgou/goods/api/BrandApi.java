package com.changgou.goods.api;

import com.changgou.common.entity.Result;
import com.changgou.goods.pojo.Brand;
import io.swagger.annotations.*;

import java.util.Map;

/**
 * @Description:
 * @Version: V1.0
 */
@Api(value="品牌管理接口",description = "品牌管理接口，提供页面的增、删、改、查")
public interface BrandApi {


    /**
     * 查询全部数据
     *
     * @return
     */
    @ApiOperation("查询所有品牌列表")
    public Result findAll();

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @ApiOperation("根据ID查询品牌数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true,paramType="path",dataType="int"),
    })
    public Result findById(Integer id);


    /***
     * 新增数据
     * @param brand
     * @return
     */
    @ApiOperation("新增品牌")
    public Result add(Brand brand);


    /***
     * 修改数据
     * @param brand
     * @param id
     * @return
     */
    @ApiOperation("修改品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true, paramType = "path", dataTypeClass = Integer.class),
    })
    public Result update(Brand brand, Integer id);


    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @ApiOperation("删除品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true,paramType="path",dataType="int"),
    })
    public Result delete(Integer id);

    /***
     * 多条件搜索品牌数据
     * @param searchMap
     * @return
     */
    @ApiOperation("查询品牌列表")
    public Result findList(Map searchMap);


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("条件分页查询品牌列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="searchMap",value = "条件查询 JSON数据， 品牌名称或者是品牌首字母"),
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int")
    })
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message="Indicates ..."),
//            @ApiResponse(code = 404, message = "not found error")
//    })
    public Result findPage(Map searchMap, Integer page, Integer size);

    /**
     * 根据分类名称查询品牌名称和img
     * @param categoryName
     * @return
     */
    @ApiOperation("根据分类名称查询品牌名称和图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name="categoryName",value = "categoryName",required=true,paramType="path",dataType="string"),
    })
    public Result findListByCategoryName(String categoryName);
}
