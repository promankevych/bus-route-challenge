package com.goeuro.web.controller;

import com.goeuro.service.BusRouteService;
import com.goeuro.web.response.DirectRouteCheckResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class BusRouteController {

    @Autowired
    private BusRouteService busRouteService;

    @GetMapping(value = "/direct", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DirectRouteCheckResponse> directRouteCheck(@RequestParam("dep_sid") Integer departureSId,
                                                                     @RequestParam("arr_sid") Integer arrivalSId) {
        return ResponseEntity.ok(busRouteService.checkForDirectRoute(departureSId, arrivalSId));
    }
}
