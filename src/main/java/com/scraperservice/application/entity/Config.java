package com.scraperservice.application.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "configuration")
@Data
public class Config implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer user_id;
    private String name;
    private String base_url;
    private boolean browser_automation;
    private String category_selector;
    private String subcategory_selector;
    private String product_selector;
    private String next_page_selector;
    private String next_page_get;
    private String product_indicator;
    private String product_data_selectors;
}
