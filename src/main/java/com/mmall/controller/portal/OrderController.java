package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.security.krb5.Config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;


@Controller
@RequestMapping("/order/")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    IOrderService iOrderService;

    //查询订单列表
    @RequestMapping(value="list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse list(HttpSession session, @RequestParam(value="pageNum",defaultValue = "1")int pageNum,@RequestParam(value="pageSize",defaultValue = "10")int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }

    //创建订单
    @RequestMapping(value="create.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse create(HttpSession session,Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(),shippingId);
    }

    //取消订单
    @RequestMapping(value="cancel.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse create(HttpSession session,Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancelOrder(user.getId(),orderNo);
    }

    //查看购物车详情
    @RequestMapping(value="get_order_cart_product.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    //查看订单详情
    @RequestMapping(value="details.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse details(HttpSession session,Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getDetails(user.getId(),orderNo);
    }

    @RequestMapping(value="pay.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse pay(HttpSession session,HttpServletRequest request,Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(user.getId(),orderNo,path);
    }

    @RequestMapping(value="alipay_callback.do",method = RequestMethod.POST)
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();
        Map requestMap = request.getParameterMap();
        for(Iterator iter = requestMap.keySet().iterator();iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String[])requestMap.get(name);
            String valuesString = "";
            for(int i = 0;i < values.length ;i++){
                valuesString = (i == values.length - 1)?(valuesString+values[i]):(valuesString+values[i]+",");
            }
            params.put(name,valuesString);
        }
        logger.info("支付宝回调:sign={},trade_status={},参数={}",params.get("sign"),params.get("trade_status"),params.toString());
        params.remove("sign_type");
        try {
            Boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params,Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(!alipayRSACheckedV2){
                return ServerResponse.createByErrorMessage("非法请求，再请求我就报警找网警了");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝回调异常",e);
        }
        //验证验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
        // 并判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
        // 同时需要校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方
        ServerResponse serverResponse = iOrderService.dealAlipayCallback(params);
        if(serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    @RequestMapping("get_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> getOrderPayStatus(HttpSession session,Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse = iOrderService.getOrderPayStatus(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}
