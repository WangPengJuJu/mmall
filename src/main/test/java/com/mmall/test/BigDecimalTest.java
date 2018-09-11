package com.mmall.test;

import org.junit.Test;

import java.math.BigDecimal;

public class BigDecimalTest {
    @Test
    public void test01(){
        System.out.println(0.05+0.01);
        System.out.println(1.0-0.42);
        System.out.println(4.015*100);
        System.out.println(123.3/100);
    }

    @Test
    public void test02(){
        BigDecimal bigDecimal1 = new BigDecimal(1.21);
        BigDecimal bigDecimal2 = new BigDecimal(1.3);
        System.out.println(bigDecimal1.add(bigDecimal2));
    }

    //当进行浮点数运算时要使用BigDecimal的字符串构造器
    @Test
    public void test03(){
        BigDecimal bigDecimal1 = new BigDecimal("1.21");
        BigDecimal bigDecimal2 = new BigDecimal("1.3");
        System.out.println(bigDecimal1.add(bigDecimal2));
    }
}
