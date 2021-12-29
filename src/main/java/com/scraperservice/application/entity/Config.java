package com.scraperservice.application.entity;

import javax.persistence.*;

@Entity
@Table(name = "configuration")
public class Config {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer user_id;
    private String name;
    private String base_url;

    public Config() {}
    public Config(int id, int user_id, String name, String base_url) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        this.base_url = base_url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBase_url() {
        return base_url;
    }

    public void setBase_url(String base_url) {
        this.base_url = base_url;
    }
}
