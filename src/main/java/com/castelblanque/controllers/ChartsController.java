package com.castelblanque.controllers;

import com.castelblanque.generated.ChartsApi;
import com.castelblanque.generated.model.ChartInfo;
import com.castelblanque.generated.model.ChartInstallationRequestData;
import com.castelblanque.generated.model.ChartRequest;
import com.castelblanque.services.ChartsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

    @RequestMapping(value = "/charts/uninstall/{releaseName}",
            produces = {"application/json"},
            method = RequestMethod.GET)
    public ResponseEntity<StreamingResponseBody> uninstallRelease(@PathVariable String releaseName) throws InterruptedException, IOException {

        final Runtime r = Runtime.getRuntime();
        final Process p = r.exec("helm uninstall " + releaseName);
        p.waitFor();

        final BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        final BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        final StreamingResponseBody responseBody = outputStream -> {
            // Read the output from the command
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                outputStream.write((s + "\n").getBytes(StandardCharsets.UTF_8));
            }

            System.out.println("Here is the standard error of the command (if any):");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
                outputStream.write((s + "\n").getBytes(StandardCharsets.UTF_8));
            }

        };

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseBody);

    }


    @RequestMapping(value = "/charts/new",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.POST)
    public ResponseEntity<StreamingResponseBody> installChart(@RequestBody ChartRequest chartRequest) throws InterruptedException, IOException {
        
        // @TODO Many things...but also to check that the requested chart is among the valid ones in the system

        // Add repo first
        final Runtime r2 = Runtime.getRuntime();
        final Process p2 = r2.exec("helm repo add bitnami https://charts.bitnami.com/bitnami");
        p2.waitFor();


        final StreamingResponseBody responseBody = outputStream -> {

            final Runtime r = Runtime.getRuntime();
            final Process p = r.exec("helm install " + chartRequest.getName().toLowerCase() + " " + chartRequest.getId());

            try {

                final BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                try (InputStream is = p.getInputStream()) {

                    //   is.transferTo(outputStream);

                    byte[] buf = new byte[8192];
                    int length;
                    while ((length = is.read(buf)) > 0) {
                        outputStream.write(buf, 0, length);
                    }
                }

                p.waitFor();

                String s = null;
                System.out.println("Standard error of the command (if any):");
                while ((s = stdError.readLine()) != null) {
                    System.out.println(s);
                    outputStream.write((s + "<br/>").getBytes(StandardCharsets.UTF_8));
                }

                /*final BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                final BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                // Read the output from the command
                String s = null;
                while ((s = stdInput.readLine()) != null) {
                    System.out.println(s);
                    outputStream.write((s + "<br/>").getBytes(StandardCharsets.UTF_8));
                }

                System.out.println("Here is the standard error of the command (if any):\n");
                while ((s = stdError.readLine()) != null) {
                    System.out.println(s);
                    outputStream.write((s + "<br/>").getBytes(StandardCharsets.UTF_8));
                }*/


            } catch (InterruptedException e) {
                e.printStackTrace();
                p.destroy();
            }

        };

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseBody);
    }

}
