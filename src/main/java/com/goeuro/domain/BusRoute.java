package com.goeuro.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class BusRoute {

    private Integer routeId;
    private Set<Integer> stationIds;

}
