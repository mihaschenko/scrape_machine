package com.scraperservice.application.controller;

import com.scraperservice.application.entity.Run;
import com.scraperservice.application.service.RunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RunController {
    @Autowired
    private RunService runService;

    @PostMapping(value = "/run", params = {"hash"})
    public ResponseEntity<String> getRunByHash(@RequestParam("hash") String hash) {
        Run run = runService.getRunByHash(hash);
        if(run != null) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
