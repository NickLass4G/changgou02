package com.changgou.common.model.response.goods;


import com.changgou.common.model.response.ResultCode;
import lombok.ToString;

@ToString
public enum GoodsCode implements ResultCode {

    GOODS_BRAND_ADD_ERROR(false,22001,"商品添加失败"),
    GOODS_SPU_ADD_ERROR(false,22002,"商品添加不能为空"),
    GOODS_SKU_ADD_ERROR(false,22003,"库存量添加不能为空"),
    GOODS_SPU_NOT_FOUND_ERROR(false,22004,"商品不存在"),
    GOODS_SPU_IS_DELETE_ERROR(false,22005,"商品处于删除状态"),
    GOODS_SPU_PULL_REPEAT_ERROR(false,22006,"商品已经下架,请勿重复操作"),
    GOODS_SPU_IS_EXAMINE_ERROR(false,22007,"商品正在审核,不能上架"),
    GOODS_SPU_DELETE_ERROR(false,22008,"请先下架商品"),
    GOODS_SPU_RESTORE_ERROR(false,22009,"商品未被删除");

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private GoodsCode(boolean success,int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }
    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }


}
