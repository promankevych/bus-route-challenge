package com.goeuro.service.impl;

import com.goeuro.domain.BusRoute;
import com.goeuro.service.BusRouteService;
import com.goeuro.web.response.DirectRouteCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BusRouteServiceImpl implements BusRouteService {

    @Autowired
    private BusRouteHolder busRouteHolder;

    @Override
    public DirectRouteCheckResponse checkForDirectRoute(Integer departureSId, Integer arrivalSId) {
        log.info("Going to check if there are some direct routes between {} and {} stations", departureSId, arrivalSId);
        List<BusRoute> busRoutes = busRouteHolder.getRoutes();
        List<BusRoute> directBusRoutes = getDirectRoutes(busRoutes, departureSId, arrivalSId);
        log.info("Found {} direct routes between {} and {} stations", directBusRoutes.size(), departureSId, arrivalSId);
        return DirectRouteCheckResponse.builder()
                .departureSid(departureSId)
                .arrivalSid(arrivalSId)
                .directBusRoute(!directBusRoutes.isEmpty())
                .build();
    }

    private List<BusRoute> getDirectRoutes(List<BusRoute> busRoutes, Integer departureSId, Integer arrivalSId) {
        Predicate<BusRoute> isDirectRoute = busRoute -> busRoute.getStationIds().contains(departureSId)
                && busRoute.getStationIds().contains(arrivalSId);
        return busRoutes.stream()
                .filter(isDirectRoute)
                .collect(Collectors.toList());
    }

}
