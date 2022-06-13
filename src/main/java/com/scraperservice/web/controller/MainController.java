package com.scraperservice.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RestController
public class MainController {
    @GetMapping("/")
    public ModelAndView getIndex() {
        return new ModelAndView("index");
    }
}
