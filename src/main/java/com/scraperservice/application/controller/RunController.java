package com.scraperservice.application.controller;

import com.scraperservice.application.service.RunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RunController {
    @Autowired
    private RunService runService;

    @PostMapping(value = "/run", params = {"hash"})
    public String getRunByHash(@RequestParam("hash") String hash, Model model) {
        model.addAttribute("run", runService.getRunByHash(hash));
        return "run";
    }
}
