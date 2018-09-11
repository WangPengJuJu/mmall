package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Requirement;
import com.mmall.pojo.Scene;
import com.mmall.pojo.User;
import com.mmall.service.ISceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/scene/")
public class SceneController {
    @Autowired
    ISceneService iSceneService;

    @RequestMapping(value="add.do",method = RequestMethod.POST )
    @ResponseBody
    public ServerResponse add(HttpSession session, Scene scene){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登陆");
        }
        return iSceneService.add_scene(scene);
    }
    @RequestMapping(value="delete.do",method = RequestMethod.POST )
    @ResponseBody
    public ServerResponse delete(HttpSession session,@RequestParam(value="sceneIds[]") Integer[] sceneIds){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登陆");
        }
        return iSceneService.delete_scene(sceneIds);
    }
    @RequestMapping(value="details.do",method = RequestMethod.POST )
    @ResponseBody
    public ServerResponse details(HttpSession session,Integer sceneId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登陆");
        }
        return iSceneService.getSceneDetails(sceneId);
    }
    @RequestMapping(value="findAllScenes.do",method= RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> findAllScenes(HttpSession session,@RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value="pageSize",defaultValue = "1") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登陆");
        }
        return iSceneService.findAllScenes(pageNum,pageSize);

    }


    //--------------需求---------------------------------------------------------------------------------------------------------
    @RequestMapping(value="requirements_details.do",method = RequestMethod.POST )
    @ResponseBody
    public ServerResponse requirements_details(HttpSession session,Integer requirementsId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登陆");
        }
        return iSceneService.getRequirementsDetails(requirementsId);
    }
    @RequestMapping(value="findAllRequirements.do",method= RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> findAllRequirements(HttpSession session,@RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value="pageSize",defaultValue = "1") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登陆");
        }
        return iSceneService.findAllRequirements(pageNum,pageSize);

    }
    @RequestMapping(value="add_requirements.do",method = RequestMethod.POST )
    @ResponseBody
    public ServerResponse add_requirements(Requirement requirements){
        return iSceneService.add_requirements(requirements);
    }
}
