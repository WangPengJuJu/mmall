package com.mmall.common;

/**
 * @Author wangpengju
 * @Description:
 * @Date:Created in 22:32 2018/6/7
 * @Modifided By:
 */
public enum ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");
    private final int code;
    private final String desc;
    ResponseCode(int code,String desc){
        this.code = code;
        this.desc = desc;
    }
    public int getCode(){
        return this.code;
    }
    public String getDesc(){
        return this.desc;
    }

}
