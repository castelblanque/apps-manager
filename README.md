# apps-manager

Apps manager is a backend application written in Java 1.8 and Spring Boot.

It serves a list of Helm charts that can be deployed.
Exposes an endpoint to install the requested chart.

## Build

    `mvn clean package`

## CI

There is a GitHub action to build the image and push it to Docker hub

  [Docker hub repo](https://hub.docker.com/r/castelblanque/apps-manager)
