package com.goeuro.service;

import com.goeuro.web.response.DirectRouteCheckResponse;

public interface BusRouteService {

    DirectRouteCheckResponse checkForDirectRoute(Integer departureSId, Integer arrivalSId);

}
