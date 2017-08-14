package com.goeuro.service.impl;

import com.goeuro.domain.BusRoute;
import com.goeuro.service.parser.BusRouteParsingService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@CacheConfig(cacheNames = "busRoutes")
public class BusRouteHolder {

    private static final String CACHE_KEY = "'allRoutes'";

    @Getter
    private volatile String busRouteDataPath;

    @Autowired
    private BusRouteParsingService busRouteParsingService;

    @CacheEvict(key = CACHE_KEY)
    public void setDataPath(String busRouteDataPath) {
        log.info("Set bus route data path = {} and clear routes cache", busRouteDataPath);
        this.busRouteDataPath = busRouteDataPath;
    }

    @Cacheable(key = CACHE_KEY)
    public List<BusRoute> getRoutes() {
        return busRouteParsingService.parse(busRouteDataPath);
    }

}
