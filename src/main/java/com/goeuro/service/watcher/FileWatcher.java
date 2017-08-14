package com.goeuro.service.watcher;

import com.goeuro.BusRouteApplication;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class FileWatcher extends Thread {

    private final String dataPath;
    private Path path;
    private String filename;
    private AtomicBoolean stop = new AtomicBoolean(false);
    private OnChangeAction onChange;

    public FileWatcher(String dataPath, OnChangeAction onChange) {
        this.setName(BusRouteApplication.WATCH_THREAD_NAME);
        this.dataPath = dataPath;
        this.path = Paths.get(dataPath).getParent();
        this.filename = dataPath.replace(path.toString(), "");
        this.onChange = onChange;
    }

    public boolean isStopped() {
        return stop.get();
    }

    public void stopWatch() {
        stop.set(true);
    }

    private void doOnChange() {
        log.info("Changes detected on target file with name: {}", filename);
        // I think that it is better way to just clean cache instead of restarting application
        this.onChange.apply(this.dataPath);
    }

    @Override
    public void run() {
        log.info("Going to watch for changes in file: {}", dataPath);
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            this.path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

            while (!isStopped()) {
                WatchKey key;
                try {
                    key = watcher.poll(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.error("Error occurs during polling modify events");
                    return;
                }
                if (Objects.nonNull(key)) {
                    processEvents(key);
                }
                Thread.yield();
            }
        } catch (Exception e) {
            log.error("Something went wrong with watch thread", e);
        }
    }

    private void processEvents(WatchKey key) {
        for (WatchEvent<?> event : key.pollEvents()) {
            if (targetFileChanged(event)) {
                doOnChange();
            }
            if (!key.reset()) {
                break;
            }
        }
    }

    private boolean targetFileChanged(WatchEvent<?> event) {
        WatchEvent.Kind<?> kind = event.kind();
        WatchEvent<Path> ev = (WatchEvent<Path>) event;
        return StandardWatchEventKinds.ENTRY_MODIFY.equals(kind) && this.filename.equals(ev.context().toString());
    }
}
