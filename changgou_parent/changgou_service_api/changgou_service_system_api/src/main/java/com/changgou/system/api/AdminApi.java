package com.changgou.system.api;

import com.changgou.common.entity.Result;
import com.changgou.system.pojo.Admin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

/**
 * @Description:
 * @Version: V1.0
 */
@Api(value="管理员管理接口",description = "管理员管理接口，提供页面的增、删、改、查")
public interface AdminApi {


    /**
     * 查询全部数据
     *
     * @return
     */
    @ApiOperation("查询所有管理员列表")
    public Result findAll();

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @ApiOperation("根据ID查询管理员数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true,paramType="path",dataType="int"),
    })
    public Result findById(Integer id);


    /***
     * 新增数据
     * @param admin
     * @return
     */
    @ApiOperation("新增管理员")
    public Result add(Admin admin);


    /***
     * 修改数据
     * @param admin
     * @param id
     * @return
     */
    @ApiOperation("修改管理员")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true, paramType = "path", dataTypeClass = Integer.class),
    })
    public Result update(Admin admin, Integer id);


    /***
     * 根据ID删除管理员数据
     * @param id
     * @return
     */
    @ApiOperation("删除管理员")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true,paramType="path",dataType="int"),
    })
    public Result delete(Integer id);

    /***
     * 多条件搜索管理员数据
     * @param searchMap
     * @return
     */
    @ApiOperation("查询管理员列表")
    public Result findList(Map searchMap);


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("条件分页查询管理员列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int")
    })
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message="Indicates ..."),
//            @ApiResponse(code = 404, message = "not found error")
//    })
    public Result findPage(Map searchMap, Integer page, Integer size);

    @ApiOperation("管理员登录")
    public Result login(Admin admin);

}
