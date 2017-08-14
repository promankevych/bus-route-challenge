package com.goeuro.service.parser;

import java.util.List;

@FunctionalInterface
public interface RouteParsingService<T> {

    List<T> parse(String path);

}
