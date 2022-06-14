package com.scraperservice.web.service;

import com.scraperservice.web.entity.Config;
import com.scraperservice.web.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ConfigJpaService")
public class ConfigJpaService implements com.scraperservice.web.service.Service<Config> {
    @Autowired
    private ConfigRepository repository;

    @Override
    public List<Config> getAll() {
        return repository.findAll();
    }

    @Override
    public Config getById(int id) {
        return repository.getById(id);
    }

    @Override
    public void delete(int id) {
        repository.deleteById(id);
    }

    @Override
    public void update(Config config) {
        repository.save(config);
    }
}
