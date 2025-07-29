# Stage 1: Build Nuxt Static Site
FROM node:22-alpine AS build
WORKDIR /app

RUN corepack enable
COPY package.json package-lock.json ./
RUN npm install

COPY . .
RUN npm run build

# Stage 2: Static file server
FROM nginx:alpine
COPY --from=build /app/.output/public /usr/share/nginx/html

# Optional: Replace default nginx config (optional)
# COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
