package com.changgou.goods.api;

import com.changgou.common.entity.Result;
import com.changgou.goods.pojo.Goods;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.Map;

/**
 * @Description:
 * @Version: V1.0
 */
@Api(value="SPU(标准产品单位)管理接口",description = "SPU(标准产品单位)管理接口，提供页面的增、删、改、查")
public interface SpuApi {


    /**
     * 查询全部数据
     *
     * @return
     */
    @ApiOperation("查询所有SPU(标准产品单位)列表")
    public Result findAll();

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @ApiOperation("根据ID查询SPU(标准产品单位)数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true,paramType="path",dataType="string")
    })
    public Result findById(String id);


    /***
     * 新增数据
     * @param goods
     * @return
     */
    @ApiOperation("新增SPU(标准产品单位)")
    public Result add(Goods goods);


    /***
     * 修改数据
     * @param goods
     * @param id
     * @return
     */
    @ApiOperation("修改SPU(标准产品单位)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true, paramType = "path", dataType="string")
    })
    public Result update(Goods goods, String id);


    /***
     * 根据ID删除SPU(标准产品单位)数据
     * @param id
     * @return
     */
    @ApiOperation("删除SPU(标准产品单位)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true,paramType="path",dataType="string")
    })
    public Result delete(String id);

    /***
     * 多条件搜索SPU(标准产品单位)数据
     * @param searchMap
     * @return
     */
    @ApiOperation("查询SPU(标准产品单位)列表")
    public Result findList(Map searchMap);


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("条件分页查询SPU(标准产品单位)列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int")
    })

    public Result findPage(Map searchMap, Integer page, Integer size);

    /**
     * 商品审核
     * @param id
     * @return
     */
    @ApiOperation("根据ID审核商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true,paramType="path",dataType="string")
    })
    public Result examine(String id);

    /**
     * 商品下架
     * @param id
     * @return
     */
    @ApiOperation("根据ID下架商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true,paramType="path",dataType="string")
    })
    public Result pull(String id);

    /**
     * 商品上架
     * @param id
     * @return
     */
    @ApiOperation("根据ID上架商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true,paramType="path",dataType="string")
    })
    public Result put(String id);

    /**
     * 商品还原
     * @param id
     * @return
     */
    @ApiOperation("根据ID还原商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true,paramType="path",dataType="string")
    })
    public Result restore(String id);


    /**
     * 商品物理删除
     * @param id
     * @return
     */
    @ApiOperation("根据ID彻底删除商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value = "id",required=true,paramType="path",dataType="string")
    })
    public Result realDelete(String id);
}
