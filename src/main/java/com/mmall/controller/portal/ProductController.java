package com.mmall.controller.portal;

import com.mmall.common.ServerResponse;
import com.mmall.service.IproductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    IproductService iProductService;
    @RequestMapping(value="get_product_detail.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getProductDetail(HttpSession session, Integer productId){
        return iProductService.getProductDetail(productId);
    }

    @RequestMapping(value="list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse list(@RequestParam(value="keyword",required=false)String keyword,
            @RequestParam(value="categoryId",required=false)Integer categoryId,
            @RequestParam(value="pageSize",defaultValue="10")Integer pageSize,
            @RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
            @RequestParam(value="orderby",defaultValue = "")String orderby){
        return iProductService.list(keyword,categoryId,pageSize,pageNum,orderby);
    }
}
