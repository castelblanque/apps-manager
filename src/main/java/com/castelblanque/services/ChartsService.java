package com.castelblanque.services;

import com.castelblanque.generated.model.ChartInstallationRequestData;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

@Service
public class ChartsService {

    public ChartInstallationRequestData install(String id, String name) {
        final ChartInstallationRequestData outcome = new ChartInstallationRequestData();
        outcome.setId(UUID.randomUUID());
        return outcome;
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
