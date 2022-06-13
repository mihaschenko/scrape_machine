package com.scraperservice.web.entity;

import com.scraperservice.web.ConnectWay;
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
    @Column(name = "user_id")
    private Integer userId;
    private String name;
    @Column(name = "base_url")
    private String baseUrl;
    // FIXME need to fix connecting to data base
    @Column(name = "browser_automation")
    private ConnectWay connectWay;
    @Column(name = "category_selector")
    private String categorySelector;
    @Column(name = "subcategory_selector")
    private String subcategorySelector;
    @Column(name = "product_selector")
    private String productSelector;
    @Column(name = "next_page_selector")
    private String nextPageSelector;
    @Column(name = "next_page_get")
    private String nextPageGet;
    @Column(name = "product_indicator")
    private String productIndicator;
    @Column(name = "product_data_selectors")
    private String productDataSelectors;
}
