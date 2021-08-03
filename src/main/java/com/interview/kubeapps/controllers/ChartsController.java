package com.interview.kubeapps.controllers;

import com.interview.kubeapps.generated.ChartsApi;
import com.interview.kubeapps.generated.model.ChartInfo;
import com.interview.kubeapps.generated.model.ChartInstallationRequestData;
import com.interview.kubeapps.generated.model.ChartRequest;
import com.interview.kubeapps.services.ChartsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    @RequestMapping(value = "/charts/new",
            produces = { "application/json" },
            consumes = { "application/json" },
            method = RequestMethod.POST)
    public ResponseEntity<StreamingResponseBody> installChart(@RequestBody ChartRequest chartRequest) throws InterruptedException, IOException {

        Runtime r2 = Runtime.getRuntime();
        Process p2 = r2.exec("helm repo add bitnami https://charts.bitnami.com/bitnami");
        p2.waitFor();


        StreamingResponseBody responseBody = outputStream -> {

            try {
                Runtime r = Runtime.getRuntime();
                Process p = r.exec("helm install " + chartRequest.getName() + " " + chartRequest.getId());
                p.waitFor();

                int numberOfBytesToWrite;
                byte[] data = new byte[1024];
                while ((numberOfBytesToWrite = p.getInputStream().read(data, 0, data.length)) != -1) {
                    System.out.println("Writing some bytes..");
                    outputStream.write(data, 0, numberOfBytesToWrite);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        };

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseBody);
    }

}
