package com.interview.kubeapps.controllers;

import java.util.Arrays;
import java.util.List;

import com.interview.kubeapps.generated.ChartsApi;
import com.interview.kubeapps.generated.model.ChartInfo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ChartsController implements ChartsApi {

    @Override
    public ResponseEntity<List<ChartInfo>> listAvailableCharts() {
        final ChartInfo wordpressChart = new ChartInfo();
        wordpressChart.setId("bitnami/wordpress");
        wordpressChart.setName("Wordpress");

        final ChartInfo drupalChart = new ChartInfo();
        drupalChart.setId("bitnami/drupal");
        drupalChart.setName("Drupal");

        return new ResponseEntity<List<ChartInfo>>(Arrays.asList(wordpressChart, drupalChart), HttpStatus.OK);
    }
    
}
