package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service("iCartService")
public class CartService implements ICartService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    //获取购物车产品数量
    public ServerResponse<Integer> getCartProductCountByUserId(Integer userId){
        if(userId == null){
            return ServerResponse.createBySuccess(0);
        }
        Integer count = cartMapper.getCartProductCountByUserId(userId);
        return ServerResponse.createBySuccess(count);
    }
    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }
    //全选，取消全选,单选，取消单选
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer checked){
        cartMapper.selectOrUnSelectAllProduct(userId,productId,checked);
        return this.list(userId);
    }
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count){
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if(cart == null){
            Cart cartItem = new Cart();
            cartItem.setChecked(Const.checkstatus.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setQuantity(count);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }else{
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    //更新产品
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count){
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if(cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }


    //删除产品
    public ServerResponse<CartVo> delete_product(Integer userId, String productIds){
        if(productIds == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        cartMapper.deleteByUserIdAndProductId(userId,productIdList);
        return this.list(userId);
    }
    public CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if(CollectionUtils.isNotEmpty(cartList)){
            for(Cart cartItem : cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());
                cartProductVo.setId(cartItem.getId());
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product != null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductSubTitle(product.getSubtitle());
                    cartProductVo.setStock(product.getStock());
                    int buyLimitCount = 0;
                    if(product.getStock() > cartItem.getQuantity()){
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_QUANTITY_SUCCESS);
                    }else{
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_QUANTITY_FAIL);
                        Cart cartQuantity = new Cart();
                        cartQuantity.setId(cartItem.getId());
                        cartQuantity.setQuantity(product.getStock());
                        cartMapper.updateByPrimaryKeySelective(cartQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity().doubleValue()));
                    //勾选
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
                if(cartItem.getChecked() == Const.checkstatus.CHECKED){
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }
    //查询购物车里面是否所有产品都被勾选
    private Boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }else{
            return cartMapper.selectProductCheckedStatusByUserId(userId) == 0;
        }
    }
}
