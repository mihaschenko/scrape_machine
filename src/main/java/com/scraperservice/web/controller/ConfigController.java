package com.scraperservice.web.controller;

import com.scraperservice.web.entity.Config;
import com.scraperservice.web.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RestController
public class ConfigController {
    @Autowired
    @Qualifier("ConfigMemoryService")
    private Service configService;

    @GetMapping("/config")
    public ModelAndView getAll() {
        List<Config> configList = configService.getAll();
        Map<String, Object> params = new HashMap<>();
        params.put("configList", configList);
        params.put("configListSize", configList.size());
        return new ModelAndView("configList", params);
    }

    @GetMapping("/config/delete")
    public ModelAndView delete(@RequestParam String id) {
        configService.delete(Integer.parseInt(id));
        return new ModelAndView("redirect:/config");
    }
}
