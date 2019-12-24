package com.changgou.common.model.response.file;


import com.changgou.common.model.response.ResultCode;
import lombok.ToString;
// 25000-- 文件系统错误代码
@ToString
public enum FileCode implements ResultCode {

    //cGOODS_BRAND_ADD_ERROR(false,22001,"商品添加失败");
    NO_SUCH_FILE(false,25001,"文件不存在");

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private FileCode(boolean success, int code, String message){
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
