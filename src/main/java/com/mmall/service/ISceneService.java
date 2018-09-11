package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Requirement;
import com.mmall.pojo.Scene;

/**
 * @Author wangpengju
 * @Description:
 * @Date:Created in 22:07 2018/6/26
 * @Modifided By:
 */
public interface ISceneService {
    ServerResponse<PageInfo> findAllScenes(int pageNum, int pageSize);

    ServerResponse add_scene(Scene scene);

    ServerResponse delete_scene(Integer[] sceneIds);

    ServerResponse getSceneDetails(Integer sceneId);


    //--------------------需求-------------------------------------

    ServerResponse getRequirementsDetails(Integer requirementsId);

    ServerResponse findAllRequirements(int pageNum, int pageSize);

    ServerResponse add_requirements(Requirement requirements);
}
