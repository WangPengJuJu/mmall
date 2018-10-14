package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JedisPoolUtil;
import com.mmall.util.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping/")
public class ShippingController {
    @Autowired
    IShippingService iShippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpServletRequest request, Shipping shipping){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.add(user.getId(),shipping);
    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse delete(HttpServletRequest request,Integer shippingId){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.deleteByUserIdAndShippingId(user.getId(),shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpServletRequest request,Shipping shipping){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.update(user.getId(),shipping);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Shipping> select(HttpServletRequest request,Integer shippingId){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.select(user.getId(),shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpServletRequest request, @RequestParam(value = "pageSize",defaultValue = "1") int pageSize,@RequestParam(value = "pageNum",defaultValue = "10") int pageNum){
        String token = CookieUtil.readLoginToken(request);
        if(org.apache.commons.lang.StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.list(user.getId(),pageSize,pageNum);
    }
}
