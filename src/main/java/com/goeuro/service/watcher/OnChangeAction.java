package com.goeuro.service.watcher;

@FunctionalInterface
public interface OnChangeAction {

    void apply(String path);
}
