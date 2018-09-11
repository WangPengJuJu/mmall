package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

public interface IOrderService {
    ServerResponse pay(Integer userId, Long orderNo, String path);
    ServerResponse dealAlipayCallback(Map<String,String> params);
    ServerResponse<Boolean> getOrderPayStatus(Integer userId,Long orderNo);
    ServerResponse<Object> createOrder(Integer userId,Integer shippingId);
    ServerResponse<String> cancelOrder(Integer userId,Long orderNo);
    ServerResponse getOrderCartProduct(Integer userId);
    ServerResponse<OrderVo> getDetails(Integer userId, Long orderNo);
    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);
    ServerResponse<PageInfo> getOrderListAdmin(int pageNum,int pageSize);
    ServerResponse<OrderVo> getOrderDetail(Long orderNo);
    ServerResponse<PageInfo> searchOrder(Long orderNo,int pageNum,int pageSize);
    ServerResponse sendOrderGoods(Long orderNo);
}
