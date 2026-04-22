# build stage
FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# production stage (nginx)
FROM nginx:alpine

# șterge config default
RUN rm /etc/nginx/conf.d/default.conf

# copiază config custom
COPY nginx.conf /etc/nginx/conf.d/default.conf

# copiază build-ul
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]