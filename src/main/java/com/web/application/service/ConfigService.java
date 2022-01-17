package com.web.application.service;

import com.web.application.entity.Config;
import com.web.application.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigService {
    @Autowired
    private ConfigRepository configRepository;

    public List<Config> getAll() {
        return configRepository.findAll();
    }

    public Config getConfigById(int id) {
        return configRepository.getById(id);
    }
}
