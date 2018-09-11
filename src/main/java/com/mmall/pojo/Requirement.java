package com.mmall.pojo;

public class Requirement {
    private Integer id;
    private String phone;
    private String requirements;
    public Requirement() {
        super();
    }

    public Requirement(Integer id,String phone,String requirements){
        this.id = id;
        this.phone = phone;
        this.requirements = requirements;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }
}
