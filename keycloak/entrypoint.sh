#!/bin/bash
set -euo pipefail

# Force start-dev so Render command overrides cannot switch Keycloak to heavy prod mode.
exec /opt/keycloak/bin/kc.sh start-dev \
  --cache=local \
  --import-realm \
  --http-enabled=true \
  --http-port="${PORT:-8080}" \
  --hostname-strict=false \
  --proxy-headers=xforwarded
