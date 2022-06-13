package com.scraperservice.web.service;

import com.scraperservice.web.entity.Config;
import com.scraperservice.web.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ConfigJpaService")
public class ConfigJpaService implements com.scraperservice.web.service.Service {
    @Autowired
    private ConfigRepository configRepository;

    @Override
    public List<Config> getAll() {
        return configRepository.findAll();
    }

    @Override
    public Config getConfigById(int id) {
        return configRepository.getById(id);
    }

    @Override
    public void delete(int id) {

    }
}
