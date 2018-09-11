package com.mmall.pojo;

import java.util.Date;

/**
 * @Author wangpengju
 * @Description:
 * @Date:Created in 21:54 2018/6/26
 * @Modifided By:
 */
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

    public String getScene_desc() {
        return scene_desc;
    }

    public void setScene_desc(String scene_desc) {
        this.scene_desc = scene_desc;
    }

    public Scene(){}
    public Scene(Integer id,String scene_name,String scene_desc, String thumb_img, String title, String sub_title, String about_products_id, Date create_time, Date update_time) {
        this.id = id;
        this.scene_name = scene_name;
        this.scene_desc = scene_desc;
        this.thumb_img = thumb_img;
        this.title = title;
        this.sub_title = sub_title;
        this.about_products_id = about_products_id;
        this.create_time = create_time;
        this.update_time = update_time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getThumb_img() {
        return thumb_img;
    }

    public void setThumb_img(String thumb_img) {
        this.thumb_img = thumb_img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getAbout_products_id() {
        return about_products_id;
    }

    public void setAbout_products_id(String about_products_id) {
        this.about_products_id = about_products_id;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }
}
