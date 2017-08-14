package com.goeuro;

import com.goeuro.service.watcher.FileWatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@Slf4j
@SpringBootApplication
public class BusRouteApplication {

    public static final String WATCH_THREAD_NAME = "FileWatcher";

    public static void main(String[] args) {
        try {
            SpringApplication.run(BusRouteApplication.class, args);
        } catch (Throwable e) {
            onApplicationGoesDown(e);
        }
    }

    private static void onApplicationGoesDown(Throwable e) {
        log.error("Application goes down", e);
        Thread.getAllStackTraces().keySet().stream()
                .filter(t -> WATCH_THREAD_NAME.equals(t.getName())).findAny()
                .ifPresent(thread -> ((FileWatcher) thread).stopWatch());
    }

}
