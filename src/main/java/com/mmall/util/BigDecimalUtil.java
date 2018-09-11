package com.mmall.util;

import java.math.BigDecimal;

public class BigDecimalUtil {
    private BigDecimalUtil(){

    }

    public static BigDecimal add(Double v1,Double v2){
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));
        return bigDecimal1.add(bigDecimal2);
    }
    public static BigDecimal sub(Double v1,Double v2){
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));
        return bigDecimal1.subtract(bigDecimal2);
    }

    public static BigDecimal mul(Double v1,Double v2){
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));
        return bigDecimal1.multiply(bigDecimal2);
    }

    public static BigDecimal div(Double v1,Double v2){
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(v1));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(v2));
        return bigDecimal1.divide(bigDecimal2,2,BigDecimal.ROUND_HALF_UP);//四舍五入，保留两位小数
    }


}
