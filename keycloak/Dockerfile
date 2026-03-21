FROM quay.io/keycloak/keycloak:26.5.5

WORKDIR /opt/keycloak
COPY realm/fitness-app-realm.json /opt/keycloak/data/import/fitness-app-realm.json

# Keep memory usage safe for Render free tier (512 MiB) to avoid OOM restarts.
ENV JAVA_OPTS_KC_HEAP="-XX:InitialRAMPercentage=20 -XX:MaxRAMPercentage=60 -XX:MinRAMPercentage=10"
ENV KC_CACHE=local
