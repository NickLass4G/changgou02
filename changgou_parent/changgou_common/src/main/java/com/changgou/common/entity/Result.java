package com.changgou.common.entity;

import lombok.Data;

/**
 * 返回结果实体类
 */
@Data
public class Result<T> {

    private boolean success;//是否成功
    private Integer code;//返回码
    private String message;//返回消息

    private T data;//返回数据

    public Result(boolean success, Integer code, String message, Object data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = (T)data;
    }

    public Result(boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public Result() {
        this.success = true;
        this.code = StatusCode.OK;
        this.message = "执行成功";
    }


}
