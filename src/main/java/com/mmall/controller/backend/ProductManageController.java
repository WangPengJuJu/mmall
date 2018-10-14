package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IUserService;
import com.mmall.service.IproductService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JedisPoolUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Controller
@RequestMapping("/manage/product_manage")
public class ProductManageController {
    @Autowired
    IUserService iUserService;
    @Autowired
    IproductService iProductService;
    @Autowired
    IFileService iFileService;
    @RequestMapping(value="add_product.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addProduct(HttpServletRequest request, Product product){
        String token = CookieUtil.readLoginToken(request);
        if(org.apache.commons.lang.StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登陆");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.addOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，无此操作权限！");
        }

    }

    @RequestMapping(value="set_sale_status.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setSaleStatus(HttpServletRequest request,Integer productId,Integer status){
        String token = CookieUtil.readLoginToken(request);
        if(org.apache.commons.lang.StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登陆");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.setSaleStatus(productId,status);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，无此操作权限！");
        }

    }

    @RequestMapping(value="get_product_detail.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getProductDetail(HttpServletRequest request,Integer productId){
        String token = CookieUtil.readLoginToken(request);
        if(org.apache.commons.lang.StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登陆");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.manageProductDetail(productId);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，无此操作权限！");
        }

    }

    @RequestMapping(value="get_product_list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getProductList(HttpServletRequest request, @RequestParam(value="pageNum",defaultValue="1") int pageNum,@RequestParam(value="pageSize",defaultValue="10") int pageSize){
        String token = CookieUtil.readLoginToken(request);
        if(org.apache.commons.lang.StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登陆");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.getProductList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，无此操作权限！");
        }

    }

    @RequestMapping(value="search_product.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse searchProduct(HttpServletRequest request,String productName,Integer productId, @RequestParam(value="pageNum",defaultValue="1") int pageNum,@RequestParam(value="pageSize",defaultValue="10") int pageSize){
        String token = CookieUtil.readLoginToken(request);
        if(org.apache.commons.lang.StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登陆");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.searchProductByNameAndId(productName,productId,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，无此操作权限！");
        }

    }

    @RequestMapping(value="upload.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(@RequestParam(value="upload_file",required=false)MultipartFile file, HttpServletRequest request){
        String token = CookieUtil.readLoginToken(request);
        if(org.apache.commons.lang.StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
        }
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登陆");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String uploadFileSrc = iFileService.upload(file,path);
            String ftpFileSrc = PropertiesUtil.getProperty("ftp.server.http.prefix")+uploadFileSrc;
            Map fileMap = new HashMap();
            fileMap.put("imgSrc",ftpFileSrc);
            return ServerResponse.createBySuccess(fileMap);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，无此操作权限！");
        }
    }

    //富文本编辑器中上传图片
    @RequestMapping(value="rich_text_img_upload.do",method = RequestMethod.POST)
    @ResponseBody
    public Map uploadRichTextImg(@RequestParam(value="upload_file",required=false)MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = new HashMap();
        String token = CookieUtil.readLoginToken(request);
        if(org.apache.commons.lang.StringUtils.isEmpty(token)){
            resultMap.put("success",false);
            resultMap.put("msg","管理员账号未登录");
            return resultMap;
        }
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","管理员账号未登录");
            return resultMap;
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String uploadFileSrc = iFileService.upload(file,path);
            String ftpFileSrc = PropertiesUtil.getProperty("ftp.server.http.prefix")+uploadFileSrc;
            if(StringUtils.isBlank(ftpFileSrc)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",ftpFileSrc);
            response.addHeader("Access-Control-Allow-headers","X-File-Name");
            return resultMap;
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","不是管理员，无此操作权限！");
            return resultMap;
        }
    }
}
