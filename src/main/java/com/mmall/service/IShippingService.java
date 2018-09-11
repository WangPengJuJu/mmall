package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

public interface IShippingService {
    ServerResponse add(Integer userId,Shipping shipping);
    ServerResponse deleteByUserIdAndShippingId(Integer userId,Integer ShippingId);
    ServerResponse update(Integer userId,Shipping shipping);
    ServerResponse select(Integer userId,Integer ShippingId);
    ServerResponse<PageInfo> list(Integer userId, Integer pageSize, Integer pageNum);
}
