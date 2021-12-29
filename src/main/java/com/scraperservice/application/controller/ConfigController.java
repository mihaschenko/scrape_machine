package com.scraperservice.application.controller;

import com.scraperservice.application.entity.Config;
import com.scraperservice.application.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ConfigController {
    @Autowired
    private ConfigService configService;

    @GetMapping("/")
    public String getAll(Model model) {
        List<Config> configList = configService.getAll();
        model.addAttribute("configList", configList);
        model.addAttribute("configListSize", configList.size());
        return "index";
    }
}
