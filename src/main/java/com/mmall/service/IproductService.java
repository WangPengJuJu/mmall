package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;

public interface IproductService {
    ServerResponse addOrUpdateProduct(Product product);
    ServerResponse<String> setSaleStatus(Integer productId,Integer status);
    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize);
    ServerResponse<PageInfo> searchProductByNameAndId(String productName,Integer productId,Integer pageNum, Integer pageSize);
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
    ServerResponse<PageInfo> list(String keyword,Integer categoryId,Integer pageSize,Integer pageNum,String orderby);
}
