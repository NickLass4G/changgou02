package com.changgou.goods.api;

import com.changgou.common.entity.Result;
import com.changgou.goods.pojo.Spec;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

/**
 * @Description:
 * @Version: V1.0
 */
@Api(value="规格管理接口",description = "规格管理接口，提供页面的增、删、改、查")
public interface SpecApi {


    /**
     * 查询全部数据
     *
     * @return
     */
    @ApiOperation("查询所有规格列表")
    public Result findAll();

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @ApiOperation("根据ID查询规格数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "规格ID",required=true,paramType="path",dataType="int"),
    })
    public Result findById(Integer id);


    /***
     * 新增数据
     * @param Spec
     * @return
     */
    @ApiOperation("新增规格")
    public Result add(Spec Spec);


    /***
     * 修改数据
     * @param Spec
     * @param id
     * @return
     */
    @ApiOperation("修改规格")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "规格ID",required=true, paramType = "path", dataTypeClass = Integer.class),
    })
    public Result update(Spec Spec, Integer id);


    /***
     * 根据ID删除规格数据
     * @param id
     * @return
     */
    @ApiOperation("删除规格")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "规格ID",required=true,paramType="path",dataType="int"),
    })
    public Result delete(Integer id);

    /***
     * 多条件搜索规格数据
     * @param searchMap
     * @return
     */
    @ApiOperation("查询规格列表")
    public Result findList(Map searchMap);


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("条件分页查询规格列表")
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
     * 根据分类名称查询规格
     * @param categoryName
     * @return
     */
    @ApiOperation("根据分类名称查询规格")
    @ApiImplicitParams({
            @ApiImplicitParam(name="categoryName",value = "分类名称",required=true,paramType="path",dataType="string"),
    })
    public Result findListBySpecName(String categoryName);


}
