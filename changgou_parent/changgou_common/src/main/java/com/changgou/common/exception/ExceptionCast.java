package com.changgou.common.exception;

import com.changgou.common.model.response.ResultCode;

/**
 * 异常捕获类
 **/
public class ExceptionCast {

    public static void cast(ResultCode resultCode){
        throw new CustomException(resultCode);
    }
}
