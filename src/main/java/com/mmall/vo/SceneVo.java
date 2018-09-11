package com.mmall.vo;

import java.util.Date;

public class SceneVo {
    private Integer id;
    private String scene_name;
    private String scene_desc;
    private String thumb_img;
    private String title;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getScene_name() {
        return scene_name;
    }

    public void setScene_name(String scene_name) {
        this.scene_name = scene_name;
    }

    public String getScene_desc() {
        return scene_desc;
    }

    public void setScene_desc(String scene_desc) {
        this.scene_desc = scene_desc;
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

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    private String sub_title;
    private String about_products_id;
    private String create_time;
    private String update_time;
}
