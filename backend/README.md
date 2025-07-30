# Backend

## Description

This is the backend part of the Java FI Fullstack application, built with Spring Boot. It provides RESTful APIs for the frontend to interact with.

### Structure

The application is structured as a typical Spring Boot project: in controllers/ you will find the REST controllers, in services/ the business logic, and in repositories/ the data access layer. Configuration files are located in src/main/resources.

---

## Run

### Docker

```shell
docker build -t java-fi-backend .
docker run -p 8080:8080 java-fi-backend   # Adjust port if needed
```
### Without Docker

```shell
mvn spring-boot:run
```

### Configuration

In the `src/main/resources/application.properties` file, you can configure various properties such as server port, database settings, and more.

---

## Instrument Validation

In `\src\main\java\com\github\sol239\javafi\backend\utils\instrument\InstrumentValidator.java`, you can find the validation logic for the instruments. You can run the validation by running the main method in the InstrumentValidator class.
It uses java's annotations to find the instruments to be validated. Further vaidation could be done in the future.  

---

## Monitoring and Visualization

The backend provides Prometheus metrics, which can be visualized using Grafana. The default port for Grafana is 3000.

### Prometheus Configuration

You can probably find Prometheus at `http://localhost:9090` (default port).

This is how could possibly look configuration file for Prometheus (`prometheus.yml`):

```yaml
# my global config
global:
  scrape_interval: 15s # Set the scrape interval to every 15 seconds. Default is every 1 minute.
  evaluation_interval: 15s # Evaluate rules every 15 seconds. The default is every 1 minute.
  # scrape_timeout is set to the global default (10s).

# Alertmanager configuration
alerting:
  alertmanagers:
    - static_configs:
        - targets:
          # - alertmanager:9093

# Load rules once and periodically evaluate them according to the global 'evaluation_interval'.
rule_files:
  # - "first_rules.yml"
  # - "second_rules.yml"

# A scrape configuration containing exactly one endpoint to scrape:
# Here it's Prometheus itself.
scrape_configs:
  # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
  - job_name: 'spring-boot-app'

    # metrics_path defaults to '/metrics'
    # scheme defaults to 'http'.

    metrics_path: '/actuator/prometheus'

    static_configs:
      - targets: ["localhost:8080"]
       # The label name is added as a label `label_name=<label_value>` to any timeseries scraped from this config.
        labels:
          app: "spring-boot-app"
```

### Grafana

You can probably find grafana at `http://localhost:3000` (default port).
Example grafana dashboard can be found in `./assets/java-fi-spring-boot-grafana.json`.

---

## Logging

The application uses SLF4J for logging. Logs are stored in `logs/` directory.

---

## Benchmarks (Performance Testing)

The application uses JMH (Java Microbenchmark Harness) for performance testing. You can find the benchmarks in benchmark/ directory (You can change the result directory in `.env` file)

You can find the benchmarks in `backend\src\main\java\com\github\sol239\javafi\backend\benchmark`.
You can run the benchmarks by running the main method in the `backend\src\main\java\com\github\sol239\javafi\backend\benchmark\Main.java`.

---

