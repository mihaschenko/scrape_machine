package com.web.application.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "run")
@Data
public class Run implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "user_id")
    private Integer userId;
    private String hash;
    private String state;
    @OneToOne
    @JoinColumn(name = "config_id")
    private Config config;
}
