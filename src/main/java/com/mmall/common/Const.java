package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author wangpengju
 * @Description:
 * @Date:Created in 23:56 2018/6/8
 * @Modifided By:
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";

    /**
     * 这里用作验证码存入session的属性名
     */
    public static final String SESSION_KEY_OF_RAND_CODE = "randCode";

    public interface RedisCacheExtime{
        int REDIS_SESSION_EXTIME = 60 * 30;//设置登陆的session有效期为30分钟
        int REDIS_RAND_CODE_EXTIME = 60 * 1;//设置验证码的有效期
    }
    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC =Sets.newHashSet("price_asc","price_desc");
   }
    //接口里面变量会被隐式指定为public static finally
    //方法则会被隐式的指定为public abstract
    public interface Role{
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管理员
    }
    public interface Cart{
        String LIMIT_QUANTITY_FAIL = "LIMIT_QUANTITY_FAIL";
        String LIMIT_QUANTITY_SUCCESS= "LIMIT_QUANTITY_SUCCESS";
    }
    public interface checkstatus{
        int CHECKED = 1;//选中状态
        int UN_CHECKED = 0;//未选中状态
    }
    public enum ProductStatusEnum{
        ON_SALE(1,"在线");
        private int code;
        private String value;
        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
    public enum OrderStatusEnum{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已支付"),
        SHIPPED(30,"已发货"),
        ORDER_SUCCESS(40,"订单已完成"),
        ORDER_CLOSED(50,"订单已关闭");

        private int code;
        private String value;

        public int getCode() {
            return code;
        }
        public String getValue() {
            return value;
        }
        OrderStatusEnum(int code, String value){
            this.code = code;
            this.value = value;
        }

        public static OrderStatusEnum getOrderStatusByCode(Integer code) {
            OrderStatusEnum defaultOrderStatusEnum = OrderStatusEnum.CANCELED;
            for (OrderStatusEnum OrderStatusEnum : OrderStatusEnum.values()) {
                if (OrderStatusEnum.code == code) {
                    return OrderStatusEnum;
                }
            }
            throw new RuntimeException("沒有找到对应的枚举");
        }

        public static String getDescByCode(Integer code) {
            return getOrderStatusByCode(code).getValue();
        }
    }
    public interface  AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum PayPlatform{
        ALIPAY(1,"支付宝"),
        WECHAT(2,"微信");
        private int code;
        private String value;
        PayPlatform(int code,String value){
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    public enum PaymentTypeEnum{
        ONLINE_PAY(1,"在线支付");
        private int code;
        private String value;
        PaymentTypeEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
        public static PaymentTypeEnum getPaymentTypeByCode(Integer code) {
            PaymentTypeEnum defaultPaymentTypeEnum = PaymentTypeEnum.ONLINE_PAY;
            for (PaymentTypeEnum paymentTypeEnum : PaymentTypeEnum.values()) {
                if (paymentTypeEnum.code == code) {
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("沒有找到对应的枚举");
        }

        public static String getDescByCode(Integer code) {
            return getPaymentTypeByCode(code).getValue();
        }
    }
}
