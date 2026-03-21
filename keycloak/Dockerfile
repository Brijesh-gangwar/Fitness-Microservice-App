FROM quay.io/keycloak/keycloak:26.5.5

COPY realm/fitness-app-realm.json /opt/keycloak/data/import/fitness-app-realm.json

CMD ["start-dev","--import-realm","--http-port=8080","--hostname-strict=false","--proxy-headers=xforwarded"]
