# Java Full Stack Application

## Description

A full-stack application built with Java Spring Boot for the backend and Nuxt for the frontend. The appliacation servers for backtesting trading strategies.
The application uses same database (H2) and backtesting logic as the application from the winter semester [java-fi](https://github.com/sol239/java-fi).

### Structure
- `./backend`: Contains the Spring Boot application. (visit [backend/README.md](./backend/README.md) for more details)
- `./frontend`: Contains the Nuxt application. (visit [frontend/README.md](./frontend/README.md) for more details)


---

## Install & Run

```shell
# Installation
git clone https://github.com/sol239/java-fi-fullstack.git
```


### Docker

```shell
# Spring Boot backend
cd backend
docker build -t java-fi-backend .
docker run -p 8080:8080 java-fi-backend   # Adjust port if needed
```

```shell
# Nuxt frontend
cd frontend
docker build -t java-fi-frontend . 
docker run -p 3001:80 java-fi-frontend 
```

> [!IMPORTANT]  
> Grafana which is used in this project uses port 3000 by default.

### Docker Compose

```shell
# Run both backend and frontend
docker-compose up --build
```


### Without Docker
```shell
# Spring Boot backend
cd backend
mvn spring-boot:run
```

```shell
# Nuxt frontend
cd frontend
npm install
npm run dev
```

---

## Known Issues

### Running frontend with docker or docker-compose.

When running the frontend with Docker or Docker Compose, you may encounter issues with the TradingView's LightweightChart's infinite data scroll feature.

### Trade markers are not updating.

If you want to display the trade markers on the chart, you have to press button **Show Markers** each time you want to see markers which were not yet visible.
The markers are not being updated automatically with the infinite scroll feature of the chart.

---

## CSV Format

You can find an example in `./backend/assets/csv/BTCUSD_1D.csv`

### Schema

| id INT | timestamp BIGINT | open DOUBLE PRECISION | high DOUBLE PRECISION | low DOUBLE PRECISION | close DOUBLE PRECISION | volume DOUBLE PRECISION | date TIMESTAMP |
|--------|------------------|-----------------------|-----------------------|----------------------|------------------------|-------------------------|----------------|

### Example

| id  | date                | open   | high   | low    | close  | volume     | timestamp  |
|-----|---------------------|--------|--------|--------|--------|------------|------------|
| 1   | 2014-11-28 00:00:00 | 363.59 | 381.34 | 360.57 | 376.28 | 3220878.18 | 1417132800 |
| 2   | 2014-11-29 00:00:00 | 376.42 | 386.60 | 372.25 | 376.72 | 2746157.05 | 1417219200 |
| ... | ...                 | ...    | ...    | ...    | ...    | ...        | ...        |

---

## Contact

If you have any questions or suggestions, feel free to contact me at email: `david.valek17@gmail.com`