package com.scraperservice.web.controller;

import com.scraperservice.web.entity.Config;
import com.scraperservice.web.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
public class ConfigController {
    @Autowired
    @Qualifier("ConfigMemoryService")
    private Service<Config> service;

    @GetMapping("/config")
    public String getAll(Model model) {
        List<Config> configList = service.getAll();
        model.addAttribute("configList", configList);
        model.addAttribute("configListSize", configList.size());
        return "config/configList";
    }

    @GetMapping("/config/delete")
    public String delete(@RequestParam String id) {
        service.delete(Integer.parseInt(id));
        return "redirect:/config";
    }

    @GetMapping("/config/create")
    public String create() {
        return "config/createUpdateConfig";
    }

    @GetMapping("/config/update")
    public String update(@RequestParam String id, Model model) {
        model.addAttribute("config", service.getById(Integer.parseInt(id)));
        return "config/createUpdateConfig";
    }

    @PostMapping("/config/update")
    public String updateOrCreate(Config config) {
        service.update(config);
        return "redirect:/config";
    }
}
