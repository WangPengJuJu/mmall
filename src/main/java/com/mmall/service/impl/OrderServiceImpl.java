package com.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.alipay.demo.trade.model.GoodsDetail;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FTPUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    //backend
    //获取后台订单列表
    public ServerResponse<PageInfo> getOrderListAdmin(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectAllOrder();
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList,null);
        PageInfo resultPage = new PageInfo(orderList);
        resultPage.setList(orderVoList);
        return ServerResponse.createBySuccess(resultPage);
    }
    //获取后台订单详情
    public ServerResponse<OrderVo> getOrderDetail(Long orderNo){
       Order order = orderMapper.selectOrderByOrderNo(orderNo);
       if(order != null){
           List<OrderItem> orderItemList = orderItemMapper.getOrderItemByOrderNo(orderNo);
            OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
            return ServerResponse.createBySuccess(orderVo);
       }
       return ServerResponse.createByErrorMessage("订单不存在");
    }
    //查询后台订单，暂时做成只根据orderNo查询的
    public ServerResponse<PageInfo> searchOrder(Long orderNo,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        Order order = orderMapper.selectOrderByOrderNo(orderNo);
        if(order != null){
            List<OrderItem> orderItemList = orderItemMapper.getOrderItemByOrderNo(orderNo);
            OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
            PageInfo resultPage = new PageInfo(Lists.newArrayList(order));
            resultPage.setList(Lists.newArrayList(orderVo));
            return ServerResponse.createBySuccess(resultPage);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }
    //发货
    public ServerResponse<String> sendOrderGoods(Long orderNo){
        Order order = orderMapper.selectOrderByOrderNo(orderNo);
        if(order != null){
            if(order.getStatus() == Const.OrderStatusEnum.PAID.getCode()){
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                order.setSendTime(new Date());
                int rowCount = orderMapper.updateByPrimaryKeySelective(order);
                if(rowCount > 0){
                    return ServerResponse.createBySuccess("发货成功");
                }
            }
            return ServerResponse.createByErrorMessage("发货失败");
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }




    //portal
    //获取订单列表
    public ServerResponse<PageInfo> getOrderList(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectOrderByUserId(userId);
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList,userId);
        PageInfo resultPage = new PageInfo(orderList);
        resultPage.setList(orderVoList);
        return ServerResponse.createBySuccess(resultPage);
    }

    private List<OrderVo> assembleOrderVoList(List<Order> orderList,Integer userId){
        List<OrderVo> orderVoList = Lists.newArrayList();
        List<OrderItem> orderItemList = Lists.newArrayList();
        for(Order order : orderList){
            if(userId == null){
                //管理员查询的时候不需要传userId
                orderItemList = orderItemMapper.getOrderItemByOrderNo(order.getOrderNo());

            }else{
                orderItemList = orderItemMapper.getByUserIdAndOrderNo(userId,order.getOrderNo());
            }
            OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }
    //获取订单详情
    public ServerResponse<OrderVo> getDetails(Integer userId,Long orderNo){
        Order order = orderMapper.selectOrderByUserIdAndOrderNo(userId,orderNo);
        if(order != null){
            List<OrderItem> orderItemList= orderItemMapper.getByUserIdAndOrderNo(userId,orderNo);
            OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("该用户没有此订单");
    }
    public ServerResponse getOrderCartProduct(Integer userId){
        OrderProductVo orderProductVo = new OrderProductVo();
        List<Cart> cartList = cartMapper.selectCartCheckedByuserId(userId);
        ServerResponse<List<OrderItem>> serverResponse = this.getCartOrderItem(userId,cartList);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList = serverResponse.getData();
        BigDecimal payment = new BigDecimal("0");
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for(OrderItem orderItem : orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(this.assembleOrderItemVo(orderItem));
        }
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImgHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        orderProductVo.setProductTotalPrice(payment);
        return ServerResponse.createBySuccess(orderProductVo);
    }
    public ServerResponse<String> cancelOrder(Integer userId,Long orderNo){
        Order order = orderMapper.selectOrderByUserIdAndOrderNo(userId,orderNo);
        if(order == null){
            return ServerResponse.createBySuccess("该用户没有此订单！");
        }
        if(order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()){
            return ServerResponse.createBySuccess("该用户订单已付款，不可取消");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int rowCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("取消订单成功");
        }
        return ServerResponse.createByErrorMessage("取消订单失败");
    }
    public ServerResponse createOrder(Integer userId,Integer shippingId){
        List<Cart> listCart = cartMapper.selectCartCheckedByuserId(userId);
        ServerResponse<List<OrderItem>> serverResponse = this.getCartOrderItem(userId,listCart);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>)serverResponse.getData();
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);
        Order order = this.assembleOrder(userId,shippingId,payment);
        if(order == null){
            return ServerResponse.createByErrorMessage("生成订单错误");
        }
        if(CollectionUtils.isEmpty(orderItemList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        for(OrderItem orderItem : orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        //使用mybatis的批量插入
        orderItemMapper.batchInsert(orderItemList);

        //生成成功，减少库存
        this.reduceProductStock(orderItemList);

        //清空购物车
        this.cleanCart(listCart);

        //组装orderVo
        OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);

    }
    //組裝orderVo
    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList){
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for(OrderItem orderItem:orderItemList){
            orderItemVoList.add(this.assembleOrderItemVo(orderItem));
        }
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setOrderItemVoList(orderItemVoList);
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.getDescByCode(order.getPaymentType()));
        orderVo.setPostage(order.getPostage());
        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(order.getUserId(),order.getShippingId());
        if(shipping != null){
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(this.assembleShippingVo(shipping));
        }
        orderVo.setShippingId(order.getShippingId());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.getDescByCode(order.getStatus()));
        return orderVo;
    }
    //组装orderItemVo
    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }
    //组装shippingVo
    private ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        return shippingVo;

    }
    private void cleanCart(List<Cart> listCart){
        for(Cart cart : listCart){
           cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }
    private void reduceProductStock(List<OrderItem> orderItemList){
        for(OrderItem orderItem : orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal payment){
        Order order = new Order();
        order.setOrderNo(this.generateOrderNo());
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);//运费暂时写死
        order.setUserId(userId);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(payment);
        order.setShippingId(shippingId);
        int rowCount = orderMapper.insert(order);
        if(rowCount > 0){
            return order;
        }
        return null;
    }
    private long  generateOrderNo(){
        long currentTime = System.currentTimeMillis();
        currentTime = currentTime + new Random().nextInt(100);
        return currentTime;
    }
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
        BigDecimal payment = new BigDecimal("0");
        for(OrderItem orderItem:orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    private ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId,List<Cart> listCart){
        List<OrderItem> orderItemList = Lists.newArrayList();
        if(CollectionUtils.isEmpty(listCart)){
            return ServerResponse.createByErrorMessage("购物车为空！");
        }
        for(Cart cartItem:listCart){
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
                return ServerResponse.createByErrorMessage("产品"+product.getName()+"不是在线售卖状态");
            }
            if(product.getStock() < cartItem.getQuantity()){
                return ServerResponse.createByErrorMessage(product.getName()+"库存不足");
            }
            orderItem.setUserId(userId);
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity().doubleValue()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }





    public ServerResponse pay(Integer userId,Long orderNo,String path){
        Map<String,String> resultMap = Maps.newHashMap();
        Order order = orderMapper.selectOrderByUserIdAndOrderNo(userId,orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("该用户没有此订单");
        }
        resultMap.put("orderNo",String.valueOf(order.getOrderNo()));
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = String.valueOf(order.getOrderNo());

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("mmall扫码支付，订单号为:").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("共花费").append(totalAmount).toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        List<OrderItem> orderItemList = orderItemMapper.getByUserIdAndOrderNo(userId,orderNo);
        for(OrderItem orderItem : orderItemList){
            GoodsDetail good = GoodsDetail.newInstance(orderItem.getProductId().toString(),orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(),orderItem.getQuantity());
            goodsDetailList.add(good);
        }
        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl("http://5evan2.natappfree.cc/order/alipay_callback.do")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        //AlipayTradeService tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                File folder = new File(path);
                if(!folder.exists()){
                    folder.setWritable(true);
                    folder.mkdirs();
                }
                String qrpath = String.format(path+"/qr-%s.png",response.getOutTradeNo());
                String filename = String.format("qr-%s.png",response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrpath);
                File targetFile = new File(path,filename);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    logger.error("上传二维码异常",e);
                }
                logger.info("qrpath:"+qrpath);
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();
                resultMap.put("qrUrl",qrUrl);
                return ServerResponse.createBySuccess(resultMap);
            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }
    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }
    public ServerResponse dealAlipayCallback(Map<String,String> params){
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        Order order = orderMapper.selectOrderByOrderNo(orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("非本商城订单");
        }
        if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess("支付宝重复调用");
        }
        if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(orderNo);
        payInfo.setPayPlatform(Const.PayPlatform.ALIPAY.getCode());
        payInfo.setPlatformStatus(tradeStatus);
        payInfo.setPlatformNumber(tradeNo);
        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();
    }

    public ServerResponse<Boolean> getOrderPayStatus(Integer userId,Long orderNo){
        Order order = orderMapper.selectOrderByOrderNo(orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("该用户没有该订单！");
        }
        if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
