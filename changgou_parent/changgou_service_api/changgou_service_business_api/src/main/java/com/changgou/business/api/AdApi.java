package com.changgou.business.api;

import com.changgou.business.pojo.Ad;
import com.changgou.common.entity.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

/**
 * @Description:
 * @Version: V1.0
 */
@Api(value="广告管理接口",description = "广告管理接口，提供页面的增、删、改、查")
public interface AdApi {


    /**
     * 查询全部数据
     *
     * @return
     */
    @ApiOperation("查询所有广告列表")
    public Result findAll();

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @ApiOperation("根据ID查询广告数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "广告ID",required=true,paramType="path",dataType="int"),
    })
    public Result findById(Integer id);


    /***
     * 新增数据
     * @param Ad
     * @return
     */
    @ApiOperation("新增广告")
    public Result add(Ad Ad);


    /***
     * 修改数据
     * @param Ad
     * @param id
     * @return
     */
    @ApiOperation("修改广告")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "广告ID",required=true, paramType = "path", dataTypeClass = Integer.class),
    })
    public Result update(Ad Ad, Integer id);


    /***
     * 根据ID删除广告数据
     * @param id
     * @return
     */
    @ApiOperation("删除广告")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "广告ID",required=true,paramType="path",dataType="int"),
    })
    public Result delete(Integer id);

    /***
     * 多条件搜索广告数据
     * @param searchMap
     * @return
     */
    @ApiOperation("查询广告列表")
    public Result findList(Map searchMap);


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("条件分页查询广告列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="searchMap",value = "条件查询 JSON数据， 广告名称或者是广告首字母"),
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int")
    })
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message="Indicates ..."),
//            @ApiResponse(code = 404, message = "not found error")
//    })
    public Result findPage(Map searchMap, Integer page, Integer size);

}
