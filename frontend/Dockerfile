FROM node:22-alpine AS builder

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install

COPY . .

RUN npm run build


FROM nginx:alpine

RUN apk add --no-cache gettext

COPY nginx.conf /etc/nginx/nginx.template

COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["/bin/sh", "-c", "envsubst '${BACKEND}' < /etc/nginx/nginx.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]