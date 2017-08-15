package com.goeuro.service.parser.impl;

import com.goeuro.domain.BusRoute;
import com.goeuro.service.parser.BusRouteParsingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class BusRouteParsingServiceImpl implements BusRouteParsingService {

    @Value("${bus.route.limit}")
    private Integer busRouteLimit;
    @Value("${bus.route.station.limit.min}")
    private Integer busRouteStationLimitMin;
    @Value("${bus.route.station.limit.max}")
    private Integer busRouteStationLimitMax;
    @Value("${bus.route.station.number.limit}")
    private Integer busRouteStationNumberLimit;

    @Override
    public List<BusRoute> parse(String path) {
        List<BusRoute> busRoutes = new LinkedList<>();

        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            Integer routesLimit = getRoutesLimit(path);

            if (routesLimitNotExceeded(routesLimit)) {
                busRoutes = parseRoutes(stream, routesLimit);
            } else {
                log.info("Routes limit from file exceeds limit from configs {} > {}", routesLimit, busRouteLimit);
            }
        } catch (Exception e) {
            log.error("Failed to process file with bus routes", e);
        }

        return busRoutes;
    }

    private List<BusRoute> parseRoutes(Stream<String> stream, Integer routesLimit) {
        return stream.limit(routesLimit + 1).skip(1)
                .map(this::convertToRoute)
                .filter(Objects::nonNull)
                .filter(distinctByKey(BusRoute::getRouteId))
                .collect(Collectors.toList());
    }

    /**
     * Current method process line from route file and map it to BusRoute object.
     * Also this method is very polite and do not throw exception it just skips invalid routes and station ids.
     *
     * @param line
     * @return bus route
     */
    private BusRoute convertToRoute(String line) {
        List<String> rowItems = Arrays.asList(line.split(" "));

        if (!line.matches("\\d.+|\\s")) {
            throw new IllegalArgumentException("File should contain only numbers");
        }

        if (stationCountIsNotAcceptable(rowItems.size() - 1)) {
            log.info("Station count is not acceptable for one route, lets skip this route");
            return null;
        }

        return parseRoute(rowItems);
    }

    private BusRoute parseRoute(List<String> rowItems) {
        Integer routeId = null;
        Set<Integer> stationIds = new HashSet<>();
        for (String item : rowItems) {
            Integer i = Integer.valueOf(item);
            if (Objects.isNull(routeId)) {
                // this code executes just once during first iteration
                routeId = i;
                continue;
            }
            if (stationNumberIsValid(i)) {
                stationIds.add(Integer.valueOf(item));
            } else {
                log.info("Oops station number {} is out of range limit is {}, lets skip it", i, busRouteStationNumberLimit);
            }
        }

        return new BusRoute(routeId, stationIds);
    }

    private Integer getRoutesLimit(String path) throws IOException {
        return Files.lines(Paths.get(path)).limit(1)
                .findFirst()
                .map(Integer::valueOf)
                .orElse(0);
    }

    private boolean routesLimitNotExceeded(Integer routesLimit) {
        return routesLimit.compareTo(busRouteLimit) <= 0 && routesLimit > 0;
    }

    private boolean stationCountIsNotAcceptable(int size) {
        return busRouteStationLimitMin.compareTo(size) >= 0 || busRouteStationLimitMax.compareTo(size) < 0;
    }

    private boolean stationNumberIsValid(Integer stationNumber) {
        return stationNumber.compareTo(busRouteStationNumberLimit) <= 0;
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> Objects.isNull(map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE));
    }
}
