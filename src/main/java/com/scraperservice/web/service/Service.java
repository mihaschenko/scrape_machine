package com.scraperservice.web.service;

import com.scraperservice.web.entity.Config;

import java.util.List;

public interface Service<T> {
    List<T> getAll();
    Config getById(int id);
    void delete(int id);
    void update(T config);
}
