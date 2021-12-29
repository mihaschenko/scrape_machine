package com.scraperservice.application.service;

import com.scraperservice.application.entity.Config;
import com.scraperservice.application.repository.ConfigRepository;
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
}
