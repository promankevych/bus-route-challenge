package com.goeuro.service.impl;

import com.goeuro.domain.BusRoute;
import com.goeuro.service.watcher.FileWatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@Slf4j
public class BusRouteLoader implements CommandLineRunner {

    @Autowired
    private BusRouteHolder busRouteHolder;

    @Override
    public void run(String... args) throws Exception {
        String dataPath = processDataPath(args);

        feedRoutesData();

        startFileWatcher(dataPath);
    }

    private String processDataPath(String... args) {
        String dataPath = args.length > 0 ? args[0] : null;

        if (StringUtils.isEmpty(dataPath)) {
            throw new IllegalArgumentException("Bus routes data path was not set, please provide it as first command line argument!");
        }

        busRouteHolder.setDataPath(dataPath);
        return dataPath;
    }

    private void feedRoutesData() {
        List<BusRoute> busRoutes = busRouteHolder.getRoutes();
        if (!CollectionUtils.isEmpty(busRoutes)) {
            log.info("Successfully put routes into cache");
        } else {
            throw new IllegalArgumentException("Something went wrong during parsing bus routes data file. Please check file content or location!");
        }
    }

    private void startFileWatcher(String dataPath) {
        FileWatcher watcher = new FileWatcher(dataPath, path -> busRouteHolder.setDataPath(dataPath));
        watcher.start();
    }
}
