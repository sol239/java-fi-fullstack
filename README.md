# Java Full Stack Application

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
docker run -p 8080:8080 java-fi-backend
```

```shell
# Nuxt frontend
cd frontend
docker build -t java-fi-frontend .
docker run -p 3001:80 java-fi-frontend
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