package com.castelblanque.controllers;

import com.castelblanque.test.TestBase;
import com.intuit.karate.junit5.Karate;

public class ChartsControllerTest extends TestBase {

    @Karate.Test
    Karate testChartsApi() {
        return Karate.run("features/charts-api.feature").relativeTo(getClass());
    }

}
