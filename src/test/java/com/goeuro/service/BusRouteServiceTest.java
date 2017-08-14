package com.goeuro.service;

import com.goeuro.domain.BusRoute;
import com.goeuro.service.impl.BusRouteHolder;
import com.goeuro.service.impl.BusRouteServiceImpl;
import com.goeuro.web.response.DirectRouteCheckResponse;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class BusRouteServiceTest {

    @InjectMocks
    private BusRouteServiceImpl busRouteService;
    @Mock
    private BusRouteHolder busRouteHolder;

    @Test
    public void testCheckForDirectRouteOnSuccess() {
        List<BusRoute> routes = new ArrayList<>();
        routes.add(new BusRoute(1, Sets.newHashSet(1, 2, 3, 4)));
        routes.add(new BusRoute(2, Sets.newHashSet(1, 2, 5, 4)));
        routes.add(new BusRoute(3, Sets.newHashSet(3, 2, 5, 4)));

        Mockito.when(busRouteHolder.getRoutes()).thenReturn(routes);

        DirectRouteCheckResponse result = busRouteService.checkForDirectRoute(1, 5);

        Assert.assertNotNull(result);
        Assert.assertEquals(Boolean.TRUE, result.getDirectBusRoute());
        Assert.assertEquals(1, result.getDepartureSid().intValue());
        Assert.assertEquals(5, result.getArrivalSid().intValue());
    }

    @Test
    public void testCheckForDirectRouteOnNoDirectRoutes() {
        List<BusRoute> routes = new ArrayList<>();
        routes.add(new BusRoute(1, Sets.newHashSet(1, 2, 3, 4)));
        routes.add(new BusRoute(2, Sets.newHashSet(1, 2, 4)));
        routes.add(new BusRoute(3, Sets.newHashSet(3, 2, 5, 4)));

        Mockito.when(busRouteHolder.getRoutes()).thenReturn(routes);

        DirectRouteCheckResponse result = busRouteService.checkForDirectRoute(1, 5);

        Assert.assertNotNull(result);
        Assert.assertEquals(Boolean.FALSE, result.getDirectBusRoute());
        Assert.assertEquals(1, result.getDepartureSid().intValue());
        Assert.assertEquals(5, result.getArrivalSid().intValue());
    }

    @Test
    public void testCheckForDirectRouteOnEmptyRoutes() {
        Mockito.when(busRouteHolder.getRoutes()).thenReturn(Collections.emptyList());

        DirectRouteCheckResponse result = busRouteService.checkForDirectRoute(1, 5);

        Assert.assertNotNull(result);
        Assert.assertEquals(Boolean.FALSE, result.getDirectBusRoute());
        Assert.assertEquals(1, result.getDepartureSid().intValue());
        Assert.assertEquals(5, result.getArrivalSid().intValue());
    }

}
