FROM clojure@sha256:b2476ab3f6fb868e7aed7ae1a7e0c216209bec254868cd41c0b0af873ea30995 AS builder

RUN curl -sL https://deb.nodesource.com/setup_16.x  | bash -
RUN apt-get -y update && apt-get -y install nodejs

WORKDIR /app/
COPY ./ /app
RUN npm config set user 0 && npm config set unsafe-perm true && npm install

RUN --mount=type=secret,id=api_config.edn \
    npx shadow-cljs release lorrem --config-merge /run/secrets/api_config.edn

FROM nginx:alpine

WORKDIR /usr/share/nginx/html/
COPY --from=builder /app/public/ ./
