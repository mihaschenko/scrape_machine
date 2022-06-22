package com.web.service;

import com.web.ConnectWay;
import com.web.entity.Config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

@Service("ConfigMemoryService")
public class ConfigMemoryService implements com.web.service.Service<Config> {
    private final Map<Integer, Config> configs = new ConcurrentHashMap<>();
    private final AtomicInteger configId = new AtomicInteger(3);

    public ConfigMemoryService() {
        Config config1 = new Config();
        config1.setId(1);
        config1.setUserId(0);
        config1.setName("Example");
        config1.setBaseUrl("https://example.com");
        config1.setConnectWay(ConnectWay.SELENIUM);
        config1.setCategorySelector("a.category_link");
        config1.setSubcategorySelector(null);
        config1.setProductSelector("a.product_link");
        config1.setNextPageGet(null);
        config1.setNextPageSelector(null);
        config1.setProductIndicator("h1[itemprop=\"name\"]");

        Config config2 = new Config();
        config2.setId(2);
        config2.setUserId(0);
        config2.setName("Example_2");
        config2.setBaseUrl("http://example_second.com");
        config2.setConnectWay(ConnectWay.JSOUP);
        config2.setCategorySelector("div.category a");
        config2.setSubcategorySelector("div.sub_category > a");
        config2.setProductSelector("div.product a");
        config2.setNextPageGet(null);
        config2.setNextPageSelector("li.current + li > a");
        config2.setProductIndicator("h1.name");

        configs.put(config1.getId(), config1);
        configs.put(config2.getId(), config2);
    }

    @Override
    public List<Config> getAll() {
        return configs.values().stream().toList();
    }

    @Override
    public Config getById(int id) {
        return configs.get(id);
    }

    @Override
    public void delete(int id) {
        configs.remove(id);
    }

    @Override
    public void update(Config config) {
        if(config.getId() == null)
            config.setId(configId.getAndIncrement());
        configs.put(config.getId(), config);
    }
}
