package com.changgou.goods.api;

import com.changgou.common.entity.Result;
import com.changgou.goods.pojo.Category;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

/**
 * @Description:
 * @Version: V1.0
 */
@Api(value="分类管理接口",description = "分类管理接口，提供页面的增、删、改、查")
public interface CategoryApi {


    /**
     * 查询全部数据
     *
     * @return
     */
    @ApiOperation("查询所有分类列表")
    public Result findAll();

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @ApiOperation("根据ID查询分类数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "分类ID",required=true,paramType="path",dataType="int"),
    })
    public Result findById(Integer id);


    /***
     * 新增数据
     * @param Category
     * @return
     */
    @ApiOperation("新增分类")
    public Result add(Category Category);


    /***
     * 修改数据
     * @param Category
     * @param id
     * @return
     */
    @ApiOperation("修改分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "分类ID",required=true, paramType = "path", dataTypeClass = Integer.class),
    })
    public Result update(Category Category, Integer id);


    /***
     * 根据ID删除分类数据
     * @param id
     * @return
     */
    @ApiOperation("删除分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "分类ID",required=true,paramType="path",dataType="int"),
    })
    public Result delete(Integer id);

    /***
     * 多条件搜索分类数据
     * @param searchMap
     * @return
     */
    @ApiOperation("查询分类列表")
    public Result findList(Map searchMap);


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("条件分页查询分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int")
    })
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message="Indicates ..."),
//            @ApiResponse(code = 404, message = "not found error")
//    })
    public Result findPage(Map searchMap, Integer page, Integer size);


    /**
     * 根据父Id查询
     * @param parentId
     * @return
     */
    @ApiOperation("根据父ID查询分类数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="parentId",value = "分类父ID",required=true,paramType="path",dataType="int"),
    })
    public Result findByParentId(Integer parentId);


}
