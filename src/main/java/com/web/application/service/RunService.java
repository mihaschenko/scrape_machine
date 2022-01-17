package com.web.application.service;

import com.web.application.entity.Run;
import com.web.application.repository.RunRepository;
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
