package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    ShippingMapper shippingMapper;
    public ServerResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount > 0){
            Map map = Maps.newHashMap();
            map.put("userId",shipping.getId());
            return ServerResponse.createBySuccess("添加地址成功！",map);
        }
        return ServerResponse.createByErrorMessage("添加地址失败！");
    }
    public ServerResponse deleteByUserIdAndShippingId(Integer userId, Integer shippingId){
        int resultCount = shippingMapper.deleteByUserIdAndShippingId(userId,shippingId);
        if(resultCount > 0){
            return ServerResponse.createBySuccess("删除地址成功！");
        }
        return ServerResponse.createByErrorMessage("删除地址失败！");
    }

    public ServerResponse update(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int resultCount = shippingMapper.updateByUserIdAndShippingId(shipping);
        if(resultCount > 0){
            return ServerResponse.createBySuccess("更新地址成功！");
        }
        return ServerResponse.createByErrorMessage("更新地址失败！");
    }

    public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(userId,shippingId);
        if(shipping != null){
            return ServerResponse.createBySuccess("查询地址成功！",shipping);
        }
        return ServerResponse.createBySuccess("查询地址失败！",shipping);
    }

    public ServerResponse<PageInfo> list(Integer userId,Integer pageSize,Integer pageNum){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
