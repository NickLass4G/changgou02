package com.changgou.common.model.response.system;

/*
*  * 26000-- 后台管理系统错误代码
 */
import com.changgou.common.model.response.ResultCode;
import lombok.ToString;

@ToString
public enum SystemCode implements ResultCode {

    SYSTEM_ADMIN_LOGIN_PASSWORD_BLANK_ERROR(false,26001,"请填写登录密码"),
    SYSTEM_ADMIN_LOGIN_USERNAME_BLANK_ERROR(false,26002,"请填写用户名称"),
    SYSTEM_ADMIN_LOGIN_COUNT_ERROR(false,26003,"无此用户"),
    SYSTEM_ADMIN_LOGIN_ERROR(false,26004,"用户名或密码不正确");

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private SystemCode(boolean success, int code, String message){
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
