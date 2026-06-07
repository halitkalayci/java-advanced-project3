# ---------- Stage 1: build the Angular app ----------
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# ---------- Stage 2: serve with nginx ----------
FROM nginx:1.27-alpine AS runtime
RUN apk add --no-cache curl
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Angular (application builder) emits to dist/<project>/browser
COPY --from=builder /app/dist/smartorder-spa/browser /usr/share/nginx/html
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/ || exit 1
