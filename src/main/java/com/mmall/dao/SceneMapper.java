package com.mmall.dao;

import com.mmall.pojo.Scene;

import java.util.List;

/**
 * @Author wangpengju
 * @Description:
 * @Date:Created in 22:01 2018/6/27
 * @Modifided By:
 */
public interface SceneMapper {
    List<Scene> findAllScenes();
    int add_scene(Scene scene);
    int delete_scene(Integer[] sceneIds);
    Scene getSceneDetailsById(Integer sceneId);
    int update_scene(Scene scene);
}
