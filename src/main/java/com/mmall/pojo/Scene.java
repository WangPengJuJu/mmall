package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @Author wangpengju
 * @Description:
 * @Date:Created in 21:54 2018/6/26
 * @Modifided By:
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Scene {
    private Integer id;
    private String scene_name;

    public String getScene_name() {
        return scene_name;
    }

    public void setScene_name(String scene_name) {
        this.scene_name = scene_name;
    }

    private String scene_desc;
    private String thumb_img;
    private String title;
    private String sub_title;
    private String about_products_id;
    private Date create_time;
    private Date update_time;

}
