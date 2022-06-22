package com.web.service;

import com.web.entity.Config;

import java.util.List;

public interface Service<T> {
    List<T> getAll();
    Config getById(int id);
    void delete(int id);
    void update(T config);
}
