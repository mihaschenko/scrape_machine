package com.scraperservice.application.service;

import com.scraperservice.application.entity.Run;
import com.scraperservice.application.repository.RunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RunService {
    @Autowired
    private RunRepository runRepository;

    public Run getRunById(int id) {
        return runRepository.getById(id);
    }

    public Run getRunByHash(String hash) {
        return runRepository.findByHash(hash);
    }
}
