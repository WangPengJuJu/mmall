package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.RequirementMapper;
import com.mmall.dao.SceneMapper;
import com.mmall.pojo.Requirement;
import com.mmall.pojo.Scene;
import com.mmall.service.ISceneService;
import com.mmall.util.DateTimeUtil;
import com.mmall.vo.SceneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author wangpengju
 * @Description:
 * @Date:Created in 22:08 2018/6/26
 * @Modifided By:
 */
@Service("iSceneService")
public class SceneServiceImpl implements ISceneService {
    @Autowired
    SceneMapper sceneMapper;
    @Autowired
    RequirementMapper requirementMapper;


    public ServerResponse<PageInfo> findAllScenes(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Scene> scenes = sceneMapper.findAllScenes();
        List<SceneVo> listSceneVos = Lists.newArrayList();
        for(Scene scene : scenes){
            SceneVo sceneVo = new SceneVo();
            sceneVo.setCreate_time(DateTimeUtil.dateToStr(scene.getCreate_time()));
            sceneVo.setId(scene.getId());
            sceneVo.setScene_name(scene.getScene_name());
            sceneVo.setScene_desc(scene.getScene_desc());
            sceneVo.setTitle(scene.getTitle());
            sceneVo.setThumb_img(scene.getThumb_img());
            listSceneVos.add(sceneVo);
        }
        PageInfo pageInfo = new PageInfo(listSceneVos);
        return ServerResponse.createBySuccess("查询场景成功",pageInfo);
    }

    public ServerResponse add_scene(Scene scene){
        if(scene.getId() != null){
            int rowCount = sceneMapper.update_scene(scene);
            if(rowCount > 0){
                return ServerResponse.createBySuccess("修改场景成功");
            }
            return ServerResponse.createByErrorMessage("修改场景失败");
        }else{
            int rowCount = sceneMapper.add_scene(scene);
            if(rowCount > 0){
                return ServerResponse.createBySuccess("添加场景成功");
            }
            return ServerResponse.createByErrorMessage("添加场景失败");
        }


    }

    public ServerResponse delete_scene(Integer[] sceneIds){
        int rowCount = sceneMapper.delete_scene(sceneIds);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("删除场景成功");
        }
        return ServerResponse.createByErrorMessage("删除场景失败");
    }
    public ServerResponse getSceneDetails(Integer sceneId){
        if(sceneId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Scene scene = sceneMapper.getSceneDetailsById(sceneId);
        if(scene == null){
            return ServerResponse.createByErrorMessage("场景已被删除");
        }
        return ServerResponse.createBySuccess(scene);
    }






    //----------------------------------------------------需求------------------------------------------------

    public ServerResponse getRequirementsDetails(Integer requirementsId){
        if(requirementsId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Requirement requirement = requirementMapper.getRequirementsDetailsById(requirementsId);
        if(requirement == null){
            return ServerResponse.createByErrorMessage("需求已被删除");
        }
        return ServerResponse.createBySuccess(requirement);
    }
    public ServerResponse<PageInfo> findAllRequirements(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Requirement> requirements = requirementMapper.findAllRequirements();
        PageInfo pageInfo = new PageInfo(requirements);
        return ServerResponse.createBySuccess("查询成功",pageInfo);
    }

    public ServerResponse add_requirements(Requirement requirements){
        int rowCount = requirementMapper.add_requirements(requirements);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("添加需求成功");
        }
        return ServerResponse.createByErrorMessage("添加需求失败");
    }
}
