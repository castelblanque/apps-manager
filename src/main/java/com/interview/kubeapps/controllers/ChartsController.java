package com.interview.kubeapps.controllers;

import com.interview.kubeapps.generated.ChartsApi;
import com.interview.kubeapps.generated.model.ChartInfo;
import com.interview.kubeapps.generated.model.ChartInstallationRequestData;
import com.interview.kubeapps.generated.model.ChartRequest;
import com.interview.kubeapps.services.ChartsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


@RestController
public class ChartsController implements ChartsApi {

    private final ChartsService chartService;

    public ChartsController(ChartsService chartService) {
        this.chartService = chartService;
    }

    @Override
    public ResponseEntity<List<ChartInfo>> listAvailableCharts() {
        final ChartInfo wordpressChart = new ChartInfo();
        wordpressChart.setId("bitnami/wordpress");
        wordpressChart.setName("Wordpress");

        final ChartInfo drupalChart = new ChartInfo();
        drupalChart.setId("bitnami/drupal");
        drupalChart.setName("Drupal");

        return new ResponseEntity<>(Arrays.asList(wordpressChart, drupalChart), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ChartInstallationRequestData> installNewChart(ChartRequest chartRequest) {
        final ChartInstallationRequestData outcome = chartService.install(chartRequest.getId(), chartRequest.getName());
        return new ResponseEntity<>(outcome, HttpStatus.OK);
    }

}
