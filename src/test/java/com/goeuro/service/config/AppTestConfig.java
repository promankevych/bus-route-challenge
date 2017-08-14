package com.goeuro.service.config;

import com.goeuro.service.parser.BusRouteParsingService;
import com.goeuro.service.parser.impl.BusRouteParsingServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppTestConfig {

    @Bean
    public BusRouteParsingService busRouteParsingService() {
        return new BusRouteParsingServiceImpl();
    }
}
