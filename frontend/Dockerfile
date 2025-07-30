# Stage 1: Build Nuxt Static Site
FROM node:22-alpine AS build
WORKDIR /app

# Enable corepack for package manager support
RUN corepack enable

# Copy only package files first for better caching
COPY package.json package-lock.json ./
RUN npm install

# Copy the rest of the application code
COPY . .

# Build the Nuxt static site in development mode for readable logs
ENV NODE_ENV=development
RUN npm run build

# Stage 2: Static file server
FROM nginx:alpine
COPY --from=build /app/.output/public /usr/share/nginx/html

# Optional: Replace default nginx config (optional)
# COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]