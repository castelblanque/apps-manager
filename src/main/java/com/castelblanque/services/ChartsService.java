package com.castelblanque.services;

import com.castelblanque.generated.model.ChartInfo;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@Service
public class ChartsService {

    @PostConstruct
    public void addHelmRepo() throws IOException, InterruptedException {
        final Runtime r2 = Runtime.getRuntime();
        final Process p2 = r2.exec("helm repo add bitnami https://charts.bitnami.com/bitnami");
        p2.waitFor();
    }


    public List<ChartInfo> getAvailableCharts() {
        final ChartInfo wordpressChart = new ChartInfo();
        wordpressChart.setId("bitnami/wordpress");
        wordpressChart.setName("Wordpress");

        final ChartInfo drupalChart = new ChartInfo();
        drupalChart.setId("bitnami/drupal");
        drupalChart.setName("Drupal");

        return Arrays.asList(wordpressChart, drupalChart);
    }

    public boolean isChartAvailable(String chartId) {
        return getAvailableCharts().stream().anyMatch(c -> c.getId().equalsIgnoreCase(chartId));
    }

    private void runCommand(String command) throws IOException, InterruptedException {
        Runtime r = Runtime.getRuntime();
        Process p = r.exec(command);
        p.waitFor();
        BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "";

        while ((line = b.readLine()) != null) {
            System.out.println(line);
        }

        b.close();
    }

}
