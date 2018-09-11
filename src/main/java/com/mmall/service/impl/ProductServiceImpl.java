package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IproductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.mmall.util.DateTimeUtil.*;

@Service("iProductService")
public class ProductServiceImpl implements IproductService {
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    ICategoryService iCategoryService;
    public ServerResponse addOrUpdateProduct(Product product){
        if(product != null){
            if(StringUtils.isNotBlank(product.getSubImages())){
                String [] subImagesArray = product.getSubImages().split(",");
                if(subImagesArray.length > 0){
                    product.setMainImage(subImagesArray[0]);
                }
            }
            if(product.getId() != null){
                int rowCount = productMapper.updateByPrimaryKeySelective(product);
                if(rowCount > 0){
                    return ServerResponse.createBySuccess("更新产品成功!");
                }
                return ServerResponse.createByErrorMessage("更新产品失败!");
            }else{
                int rowCount = productMapper.insert(product);
                if(rowCount > 0){
                    return ServerResponse.createBySuccess("新增产品成功!");
                }
                return ServerResponse.createByErrorMessage("新增产品失败!");
            }
        }
        return ServerResponse.createByErrorMessage("参数错误！");
    }

    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if(productId == null || status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }else{
            Product product = new Product();
            product.setId(productId);
            product.setStatus(status);
            int rowCount = productMapper.updateByPrimaryKeySelective(product);
            if(rowCount > 0){
                return ServerResponse.createBySuccess("更新商品状态成功");
            }
            return ServerResponse.createByErrorMessage("更新商品状态失败");
        }
    }

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }else{
            Product product = productMapper.selectByPrimaryKey(productId);
            if(product == null){
                return ServerResponse.createByErrorMessage("产品已下架或者删除");
            }
            ProductDetailVo productDetailVo = assembleProductDetailVo(product);
            return ServerResponse.createBySuccess(productDetailVo);

        }
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setName(product.getName());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setSubTitle(product.getSubtitle());
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        System.out.println("_________________________________________________________");
        System.out.println(product.getCreateTime());
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    public ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectProductList();
        List<ProductListVo> productListVoList = new ArrayList<ProductListVo>();
        for(Product productItem:productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo ProductListVo = new ProductListVo();
        ProductListVo.setId(product.getId());
        ProductListVo.setName(product.getName());
        ProductListVo.setCategoryId(product.getCategoryId());
        ProductListVo.setSubtitle(product.getSubtitle());
        ProductListVo.setPrice(product.getPrice());
        ProductListVo.setStatus(product.getStatus());
        ProductListVo.setMainImage(product.getMainImage());
        ProductListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        ProductListVo.setCreateTime(product.getCreateTime().toString());
        return ProductListVo;
    }

    public ServerResponse<PageInfo> searchProductByNameAndId(String productName,Integer productId,Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.searchProductByNameAndId(productName,productId);
        List<ProductListVo> productListVoList = new ArrayList<ProductListVo>();
        for(Product productItem:productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }else{
            Product product = productMapper.selectByPrimaryKey(productId);
            if(product == null){
                return ServerResponse.createByErrorMessage("产品已下架或者删除");
            }
            if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
                return ServerResponse.createByErrorMessage("产品已下线");
            }
            ProductDetailVo productDetailVo = assembleProductDetailVo(product);
            return ServerResponse.createBySuccess(productDetailVo);

        }
    }

    //前台查询产品列表
    public ServerResponse<PageInfo> list(String keyword,Integer categoryId,Integer pageSize,Integer pageNum,String orderby){
       if(StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
       }
       List<Integer> categoryIdList = new ArrayList<Integer>();
       if(categoryId != null){
           Category category = categoryMapper.selectByPrimaryKey(categoryId);
           //没有分类且没有关键字得时候返回一个空的结果集，不报错
           if(category == null && StringUtils.isBlank(keyword)){
               PageHelper.startPage(pageNum,pageSize);
               List<ProductListVo> productListVo = Lists.newArrayList();
               PageInfo page = new PageInfo(productListVo);
               return ServerResponse.createBySuccess(page);
           }
           categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
       }
       if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
       }
       PageHelper.startPage(pageNum,pageSize);
       if(StringUtils.isNotBlank(orderby)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderby)){
                String[] orderbyArray = orderby.split("_");
                PageHelper.orderBy(orderbyArray[0] + " " + orderbyArray[1]);
            }
       }
       List<Product> productList = productMapper.searchProductByNameAndCategoryId(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size() == 0?null:categoryIdList);
       List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product:productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
