package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.List;

/**
 * @Author wangpengju
 * @Description:
 * @Date:Created in 22:21 2018/6/7
 * @Modifided By:
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证序列化json对象的时候，如果是null的对象，key也会消失
public class ServerResponse<T> implements Serializable {
    private int status;
    private String msg;
    private T data;
    private List<T> datas;
    private ServerResponse(int status){
        this.status = status;
    }
    private ServerResponse(int status,String msg){
        this.status = status;
        this.msg = msg;
    }
    private ServerResponse(int status,T data){
        this.status = status;
        this.data = data;
    }
    private ServerResponse(int status,String msg,T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    private ServerResponse(int status,String msg,List<T> datas){
        this.status = status;
        this.msg = msg;
        this.datas = datas;
    }

    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    @JsonIgnore
    //使之不在json序列化对象当中
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }
    public int getStatus(){
        return this.status;
    }
    public T getData(){
        return this.data;
    }
    public String getMsg(){
        return this.msg;
    }
    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }
    public static <T> ServerResponse<T> createBySuccess(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }
    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
    public static <T> ServerResponse<T> createByErrorMessage(String msg){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),msg);
    }
    public static <T> ServerResponse<T> createByErrorCodeMessage(int errCode , String errorMessage){
        return new ServerResponse<T>(errCode,errorMessage);
    }
    public static <T> ServerResponse<T> createBySuccessList(String msg,List<T> datas){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,datas);
    }






}
