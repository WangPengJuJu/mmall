package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JedisPoolUtil;
import com.mmall.util.JsonUtil;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order/")
public class OrderManageController {
    @Autowired
    IUserService iUserService;
    @Autowired
    IOrderService iOrderService;
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpServletRequest request, @RequestParam(value="pageNum",defaultValue = "1")int pageNum, @RequestParam(value="pageSize",defaultValue = "10")int pageSize){
        String token = CookieUtil.readLoginToken(request);
        if(org.apache.commons.lang.StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.getOrderListAdmin(pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("不是管理员没有此操作权限");
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> detail(HttpServletRequest request, Long orderNo){
        String token = CookieUtil.readLoginToken(request);
        if(org.apache.commons.lang.StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.getOrderDetail(orderNo);
        }
        return ServerResponse.createByErrorMessage("不是管理员没有此操作权限");
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> searchOrder(HttpServletRequest request, Long orderNo,@RequestParam(value="pageNum",defaultValue = "1")int pageNum,@RequestParam(value="pageSize",defaultValue = "10")int pageSize){
        String token = CookieUtil.readLoginToken(request);
        if(org.apache.commons.lang.StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.searchOrder(orderNo,pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("不是管理员没有此操作权限");
    }
    @RequestMapping("send.do")
    @ResponseBody
    public ServerResponse<String> sendOrderGoods(HttpServletRequest request, Long orderNo){
        String token = CookieUtil.readLoginToken(request);
        if(org.apache.commons.lang.StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.sendOrderGoods(orderNo);
        }
        return ServerResponse.createByErrorMessage("不是管理员没有此操作权限");
    }
}
