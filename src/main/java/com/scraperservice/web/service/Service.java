package com.scraperservice.web.service;

import com.scraperservice.web.entity.Config;

import java.util.List;

public interface Service {
    List<Config> getAll();
    Config getConfigById(int id);
    void delete(int id);
}
