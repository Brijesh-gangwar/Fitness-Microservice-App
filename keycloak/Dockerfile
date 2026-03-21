FROM quay.io/keycloak/keycloak:26.5.5

WORKDIR /opt/keycloak
COPY realm/fitness-app-realm.json /opt/keycloak/data/import/fitness-app-realm.json
COPY --chmod=755 entrypoint.sh /opt/keycloak/entrypoint.sh

# Keep memory usage safe for Render free tier (512 MiB) to avoid OOM restarts.
ENV JAVA_OPTS_KC_HEAP="-XX:InitialRAMPercentage=15 -XX:MaxRAMPercentage=30 -XX:MinRAMPercentage=10"
ENV KC_CACHE=local

ENTRYPOINT ["/opt/keycloak/entrypoint.sh"]
