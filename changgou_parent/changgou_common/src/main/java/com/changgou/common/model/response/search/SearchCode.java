package com.changgou.common.model.response.search;


import com.changgou.common.model.response.ResultCode;
import lombok.ToString;

@ToString
public enum SearchCode implements ResultCode {

    SEARCH_SKU_EMPTY_ERROR(false,24001,"查询数据为空,不能导入索引库");


    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private SearchCode(boolean success, int code, String message){
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
