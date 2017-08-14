package com.goeuro.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DirectRouteCheckResponse {

    @JsonProperty(value = "dep_sid")
    private Integer departureSid;
    @JsonProperty(value = "arr_sid")
    private Integer arrivalSid;
    @JsonProperty(value = "direct_bus_route")
    private Boolean directBusRoute;

}
