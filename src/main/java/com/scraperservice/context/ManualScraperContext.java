package com.scraperservice.context;

import com.scraperservice.ScraperSetting;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = {"com.scraperservice"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {PresetScraperContext.class})})
public class ManualScraperContext {
    @Bean
    @Scope("singleton")
    public ScraperSetting scraperSetting() throws Exception {
        ScraperSetting scraperSetting = new ScraperSetting();
        scraperSetting.choice();
        return scraperSetting;
    }
}
