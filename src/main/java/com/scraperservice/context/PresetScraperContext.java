package com.scraperservice.context;

import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = {"com.scraperservice"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {ManualScraperContext.class})})
public class PresetScraperContext {}
