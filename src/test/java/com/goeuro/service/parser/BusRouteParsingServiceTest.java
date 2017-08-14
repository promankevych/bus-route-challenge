package com.goeuro.service.parser;

import com.goeuro.domain.BusRoute;
import com.goeuro.service.config.AppTestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;

@SpringBootTest
@ContextConfiguration(classes = AppTestConfig.class)
@RunWith(SpringRunner.class)
public class BusRouteParsingServiceTest {

    @Autowired
    private BusRouteParsingService busRouteParsingService;

    @Before
    public void init() {
        ReflectionTestUtils.setField(busRouteParsingService, "busRouteLimit", 3);
        ReflectionTestUtils.setField(busRouteParsingService, "busRouteStationLimitMin", 2);
        ReflectionTestUtils.setField(busRouteParsingService, "busRouteStationLimitMax", 10);
        ReflectionTestUtils.setField(busRouteParsingService, "busRouteStationNumberLimit", 15);
    }

    @Test
    public void testParseOnSuccess() {
        String path = getFileAbsolutePath("data/routes");

        List<BusRoute> result = busRouteParsingService.parse(path);

        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(1, result.get(0).getRouteId().intValue());
        Assert.assertEquals(9, result.get(0).getStationIds().size());
    }

    @Test
    public void testParseOnRoutesLimitExceeded() {
        String path = getFileAbsolutePath("data/routesLimitExceeded");

        List<BusRoute> result = busRouteParsingService.parse(path);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testParserOnStationLimitIsNotAcceptable() {
        String path = getFileAbsolutePath("data/routesStationLimitNotAcceptable");

        List<BusRoute> result = busRouteParsingService.parse(path);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(3, result.get(0).getRouteId().intValue());
        Assert.assertEquals(5, result.get(0).getStationIds().size());
    }

    @Test
    public void testParseOnStationNumberIsInvalid() {
        String path = getFileAbsolutePath("data/routesStationNumberInvalid");

        List<BusRoute> result = busRouteParsingService.parse(path);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(1, result.get(0).getRouteId().intValue());
        Assert.assertEquals(9, result.get(0).getStationIds().size());
    }

    @Test
    public void testParseOnInvalidDataInFile() {
        String path = getFileAbsolutePath("data/routesInvalidData");

        List<BusRoute> result = busRouteParsingService.parse(path);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    private String getFileAbsolutePath(String filename) {
        URL url = getClass().getClassLoader().getResource(filename);
        if (Objects.isNull(url)) {
            Assert.fail(String.format("Could not find target file: %s", filename));
        }
        return new File(url.getFile()).getAbsolutePath();
    }
}
