package com.changgou.common.model.response;

/**
 * 10000-- 通用错误代码
 * 22000-- 商品相关错误码
 * 23000-- 订单错误代码
 * 24000-- 搜索错误代码
 * 25000-- 文件系统错误代码
 * 26000-- 后台管理系统错误代码
 * 2700--  鉴权
 */
public interface ResultCode {
    //操作是否成功,true为成功，false操作失败
    boolean success();
    //操作代码
    int code();
    //提示信息
    String message();

}
