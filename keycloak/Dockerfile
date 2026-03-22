FROM quay.io/keycloak/keycloak:26.5.5 AS builder

WORKDIR /opt/keycloak
COPY realm/fitness-app-realm.json /opt/keycloak/data/import/fitness-app-realm.json
RUN /opt/keycloak/bin/kc.sh build --cache=local

FROM quay.io/keycloak/keycloak:26.5.5

COPY --from=builder /opt/keycloak/ /opt/keycloak/

# Keep memory usage safe for Render free tier (512 MiB) to avoid OOM restarts.
ENV JAVA_OPTS_KC_HEAP="-XX:InitialRAMPercentage=20 -XX:MaxRAMPercentage=60 -XX:MinRAMPercentage=10"
ENV KC_CACHE=local
