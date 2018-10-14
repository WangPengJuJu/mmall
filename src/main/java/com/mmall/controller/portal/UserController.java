package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.RedisPool;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JedisPoolUtil;
import com.mmall.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by wangpengju on 2018/6/5.
 */
@Controller
@RequestMapping("/user/")
public class UserController {
    /**
     *@Author wangpengju
     *@Description:
     *@Date:Created in
     *@Modifided By:
     *@params:[username, password, session]
     */
    @Autowired
    private IUserService iUserService;
    @RequestMapping(value="login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String code, String username, String password, HttpSession session, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        ServerResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){

            System.out.print("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//            System.out.print(session.getAttribute(Const.SESSION_KEY_OF_RAND_CODE));
//            if(!code.equalsIgnoreCase(session.getAttribute(Const.SESSION_KEY_OF_RAND_CODE).toString())){
//                return ServerResponse.createByErrorMessage("验证码错误");
//            }
//            session.setAttribute(Const.CURRENT_USER,response.getData());
            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
//            CookieUtil.readLoginToken(httpServletRequest);
//            CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
            JedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }
    @RequestMapping(value="loginOut.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> loginOut(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }
    @RequestMapping(value="register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        System.out.print("------------------------------------------------------------");
        return iUserService.register(user);
    }
    @RequestMapping(value="checkValid.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }
    @RequestMapping(value="getUserInfo.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest request){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);
        String token = CookieUtil.readLoginToken(request);
        String userStr = JedisPoolUtil.get(token);
        User user = JsonUtil.string2Obj(userStr,User.class);
        if(user != null){
            System.out.print("______________________________________________");
            System.out.print(user);
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
    }
    @RequestMapping(value="forgetGetQuestion.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){

        return iUserService.selectQuestion(username);
    }
    @RequestMapping(value="forgetCheckAnswer.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }
    @RequestMapping(value="forgetResetPassword.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }
    @RequestMapping(value="resetPassword.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String username,String passwordNew,String passwordOld){
        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordNew,passwordOld,user);
    }
    @RequestMapping(value="updateInfomation.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> updateInfomation(HttpSession session,User user){
        User currentUser =(User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse response =  iUserService.updateInformation(user);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }
    @RequestMapping(value="getInfomation.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getInfomation(HttpSession session){
        User currentUser =(User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登陆需要强制登陆，status=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}
