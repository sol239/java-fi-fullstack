# Frontend

## Description

This is the frontend part of the Java FI Fullstack application, built with Nuxt.js. It provides a user interface for interacting with the backend services.

### Structure

The application is structured as ordinary Nuxt.js project: ./pages contains the pages, ./components contains reusable components, and ./app/assets/css contains global styles.

---

## Run

### Docker
```shell
docker build -t java-fi-frontend . 
docker run -p 3001:80 java-fi-frontend 
# Adjust port if needed (Grafana default is 3000)
```

### Without Docker

```shell
npm install
npm run dev
```

### Port Configuration

You can define the ports in `.env` file:
```env
PORT=3005
BACKEND_BASE=http://localhost:8080
```
- Where `PORT` is the port for the Nuxt application.
- `BACKEND_BASE` is the base URL for the backend API.

---