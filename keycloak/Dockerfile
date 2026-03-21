FROM quay.io/keycloak/keycloak:26.5.5

WORKDIR /opt/keycloak
COPY realm/fitness-app-realm.json /opt/keycloak/data/import/fitness-app-realm.json

# Keep memory usage safe for Render free tier (512 MiB) to avoid OOM restarts.
ENV JAVA_OPTS_KC_HEAP="-XX:InitialRAMPercentage=20 -XX:MaxRAMPercentage=35 -XX:MinRAMPercentage=15"
ENV KC_CACHE=local

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start-dev","--cache=local","--import-realm","--http-enabled=true","--http-port=8080","--hostname-strict=false","--proxy-headers=xforwarded"]
