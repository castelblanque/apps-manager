package com.castelblanque.controllers;

import com.castelblanque.generated.ChartsApi;
import com.castelblanque.generated.model.ChartInfo;
import com.castelblanque.generated.model.ChartRequest;
import com.castelblanque.services.ChartsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Log4j2
@RestController
public class ChartsController implements ChartsApi {

    private final ChartsService chartService;

    public ChartsController(ChartsService chartService) {
        this.chartService = chartService;
    }

    @Override
    public ResponseEntity<List<ChartInfo>> listAvailableCharts() {
        return new ResponseEntity<>(chartService.getAvailableCharts(), HttpStatus.OK);
    }


    @GetMapping(value = "/charts/uninstall/{releaseName}", produces = {"application/json"})
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
                outputStream.write((s + "\n").getBytes(StandardCharsets.UTF_8));
            }

            log.debug("Here is the standard error of the command (if any):");
            while ((s = stdError.readLine()) != null) {
                log.debug(s);
                outputStream.write((s + "\n").getBytes(StandardCharsets.UTF_8));
            }

        };

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseBody);

    }


    @Override
    public ResponseEntity<StreamingResponseBody> installNewChart(@RequestBody ChartRequest chartRequest) {

        if (!chartService.isChartAvailable(chartRequest.getId())) {
            return ResponseEntity.notFound().build();
        }

        final StreamingResponseBody responseBody = outputStream -> {

            final Runtime r = Runtime.getRuntime();
            final Process p = r.exec("helm install " + chartRequest.getName().toLowerCase() + " " + chartRequest.getId());

            try {

                final BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                try (InputStream is = p.getInputStream()) {
                    byte[] buf = new byte[8192];
                    int length;
                    while ((length = is.read(buf)) > 0) {
                        outputStream.write(buf, 0, length);
                    }
                }

                p.waitFor();

                String s;
                log.debug("Standard error of the command (if any):");
                while ((s = stdError.readLine()) != null) {
                    log.debug(s);
                    outputStream.write((s + "<br/>").getBytes(StandardCharsets.UTF_8));
                }

            } catch (InterruptedException e) {
                log.error(e);
                p.destroy();
            }

        };

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseBody);
    }

}
