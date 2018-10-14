package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.JedisPoolUtil;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @Author wangpengju
 * @Description:
 * @Date:Created in 22:15 2018/6/7
 * @Modifided By:
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    UserMapper userMapper;
    public ServerResponse<User> login(String username, String password){
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String MD5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,MD5Password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功",user);
    }
    public ServerResponse<String> register(User user){
        ServerResponse<String> response = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名已存在");
        }
        response = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccess("注册成功");
    }

    public ServerResponse<String> checkValid(String str,String type){
        int resultCount;
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            if(Const.USERNAME.equals(type)){
                resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccess("校验成功");
    }
    public ServerResponse selectQuestion(String username){
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int count = userMapper.checkAnswer(username,question,answer);
        if(count > 0){
            String forgetToken = UUID.randomUUID().toString();
            JedisPoolUtil.setEx(Const.TOKEN_PREFIX + username,forgetToken,60*60*12);
            ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(org.apache.commons.lang3.StringUtils.isNotBlank(forgetToken)){
            ServerResponse.createByErrorMessage("参数错误，token需要传递！");
        }
        ServerResponse validResponse = this.checkValid(username,Const.CURRENT_USER);
        if(validResponse.isSuccess()){
            ServerResponse.createByErrorMessage("该用户不存在！");
        }
        String token = JedisPoolUtil.get(Const.TOKEN_PREFIX + username);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(token)){
            ServerResponse.createByErrorMessage("token无效或过期！");
        }
        if(org.apache.commons.lang3.StringUtils.equals(token,forgetToken)){
            String md5Password =  MD5Util.MD5EncodeUtf8(passwordNew);
            int count = userMapper.updatePasswordByUsername(username,md5Password);
            if(count > 0){
                ServerResponse.createBySuccess("重置密码成功！");
            }
        }else{
            ServerResponse.createByErrorMessage("token不一致，请重新获取token!");
        }
        return ServerResponse.createByErrorMessage("修改密码失败！");
    }
    public ServerResponse<String> resetPassword(String passwordNew,String passwordOld,User user){
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("密码错误！");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("重置密码成功！");
        }
        return ServerResponse.createByErrorMessage("重置密码失败");

    }

    public ServerResponse<User> updateInformation(User user){
        int resultCount = userMapper.checkEmailByUserId(user.getId(),user.getEmail());
        if(resultCount > 0){
            ServerResponse.createByErrorMessage("该邮箱已经存在，请更换邮箱！");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setPhone(user.getPhone());
        updateUser.setEmail(user.getEmail());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("更新接口成功！",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新接口失败！");
    }
    public ServerResponse<User> getInformation(Integer userId){
       User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }


    //后台backend
    public ServerResponse checkAdminRole(User user){
        if(user != null&&user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}
