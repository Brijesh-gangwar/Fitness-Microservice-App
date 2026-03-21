FROM quay.io/keycloak/keycloak:26.5.5 AS builder

WORKDIR /opt/keycloak
COPY realm/fitness-app-realm.json /opt/keycloak/data/import/fitness-app-realm.json
RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:26.5.5

COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start","--optimized","--import-realm","--http-enabled=true","--http-port=8080","--hostname-strict=false","--proxy-headers=xforwarded"]
