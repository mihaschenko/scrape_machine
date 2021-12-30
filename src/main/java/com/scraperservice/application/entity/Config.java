package com.scraperservice.application.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "configuration")
@Data
public class Config {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer user_id;
    private String name;
    private String base_url;
}
