package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do",method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(HttpSession session,
                                      String  categoryName,
                                      @RequestParam(value="parentId",defaultValue = "0") int parentId,
                                      @RequestParam(value="status",defaultValue = "1") int status){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        System.out.println(categoryName+"=========================");

        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登陆");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.addCategory(categoryName,parentId,status);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，无此操作权限！");
        }
    }
    @RequestMapping(value="delete.do",method = RequestMethod.POST )
    @ResponseBody
    public ServerResponse delete(HttpSession session,@RequestParam(value="categoryIds[]") Integer[] categoryIds){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登陆");
        }
        return iCategoryService.delete_category(categoryIds);
    }
    @RequestMapping("set_category_name")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session,Integer categoryId,String categoryName){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录！");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，无此操作权限！");
        }
    }

    /**
         * @Author wangpengju
         * 查询该类别id下平级子类别，不递归
         * @param  categoryId
         * @return
         * @date 2018/7/7 18:45
         */
    @RequestMapping("get_children_parallel_category")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录！");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询自己子节点的信息，只查询平级，不递归
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，无此操作权限！");
        }
    }

    /**
         * @Author wangpengju
         * 递归查询该类别id下的所有子类别
         * @param categoryId
         * @return
         * @date 2018/7/7 18:43
         */
    @RequestMapping("get_children_Deep_category")
    @ResponseBody
    public ServerResponse getChildrenDeepCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录！");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询自己子节点的信息，只查询平级，不递归
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，无此操作权限！");
        }
    }

    /**
     * @Author wangpengju
     * 递归查询该类别id下的所有子类别,包括类别名称等字段
     * @param categoryId
     * @return
     * @date 2018/7/7 18:43
     */
    @RequestMapping("get_children_Deep_category_vo")
    @ResponseBody
    public ServerResponse getChildrenDeepCategoryVo(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录！");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询自己子节点的信息，只查询平级，不递归
            return iCategoryService.selectCategoryAndChildrenVoById(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，无此操作权限！");
        }
    }

}
