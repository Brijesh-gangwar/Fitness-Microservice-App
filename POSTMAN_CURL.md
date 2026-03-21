# Fitness App OAuth2 API Guide (Render + Postman)

Last verified: 2026-03-22 (IST)

This version follows your original flow: Keycloak OAuth2 token first, then API calls through gateway with `Authorization: Bearer <token>`.

## 1) Base URLs

- `KEYCLOAK_BASE_URL`: `https://fitness-keycloak-9ucw.onrender.com`
- `GATEWAY_BASE_URL`: `https://fitness-api-gateway.onrender.com`
- `REALM`: `fitness-app`

## 2) Keycloak Bootstrap Credentials (from realm import)

After redeploying Keycloak with this repo version, these will exist automatically:

- `client_id`: `fitness-postman`
- `username`: `fitness_test_user`
- `password`: `Postman@123`

## 3) Postman Environment Variables

Create an Environment in Postman:

- `keycloak_base_url` = `https://fitness-keycloak-9ucw.onrender.com`
- `gateway_base_url` = `https://fitness-api-gateway.onrender.com`
- `realm` = `fitness-app`
- `client_id` = `fitness-postman`
- `username` = `fitness_test_user`
- `password` = `Postman@123`
- `access_token` = (set after token API)
- `keycloak_id` = (set after userinfo API)
- `activity_id` = (set after create activity API)

## 4) Get Access Token (OAuth2 Password Grant)

```bash
curl --request POST \
  --url '{{keycloak_base_url}}/realms/{{realm}}/protocol/openid-connect/token' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'client_id={{client_id}}' \
  --data-urlencode 'grant_type=password' \
  --data-urlencode 'scope=openid profile email' \
  --data-urlencode 'username={{username}}' \
  --data-urlencode 'password={{password}}'
```

From response, copy `access_token` into Postman variable `access_token`.

## 5) Get User Info (extract `sub` as `keycloak_id`)

```bash
curl --request GET \
  --url '{{keycloak_base_url}}/realms/{{realm}}/protocol/openid-connect/userinfo' \
  --header 'Authorization: Bearer {{access_token}}'
```

From response, copy `sub` into Postman variable `keycloak_id`.

## 6) Gateway Requests with Bearer Token

### 6.1 Validate (and auto-sync) User

```bash
curl --request GET \
  --url '{{gateway_base_url}}/api/users/{{keycloak_id}}/validate' \
  --header 'Authorization: Bearer {{access_token}}'
```

### 6.2 Create Activity

No `X-User-ID` needed from client side; gateway sync filter injects it from token claims.

```bash
curl --request POST \
  --url '{{gateway_base_url}}/api/activities' \
  --header 'Authorization: Bearer {{access_token}}' \
  --header 'Content-Type: application/json' \
  --data '{
    "type": "RUNNING",
    "duration": 30,
    "caloriesBurned": 240,
    "startTime": "2026-03-22T07:30:00",
    "additionalMetrics": {
      "distanceKm": 5.2,
      "avgHeartRate": 148
    }
  }'
```

If create succeeds, save response `id` as Postman `activity_id`.

### 6.3 Get User Recommendations

```bash
curl --request GET \
  --url '{{gateway_base_url}}/api/recommendations/user/{{keycloak_id}}' \
  --header 'Authorization: Bearer {{access_token}}'
```

### 6.4 Get Recommendation by Activity

```bash
curl --request GET \
  --url '{{gateway_base_url}}/api/recommendations/activity/{{activity_id}}' \
  --header 'Authorization: Bearer {{access_token}}'
```

## 7) Optional Direct Service Debug (bypasses gateway auth)

Use only for troubleshooting deployment issues:

- `https://fitness-user-service.onrender.com`
- `https://fitness-activity-service.onrender.com`
- `https://fitness-ai-service-3104.onrender.com`

## 8) Current Runtime Status

As of 2026-03-22:

- Gateway auth is token-based (`401` without bearer token).
- `user-service` is reachable.
- `activity-service` and `ai-service` are still returning `500` due MongoDB connectivity/TLS issue on Render.

## 9) Import to Postman

1. Open Postman.
2. Click `Import`.
3. Select `Raw text`.
4. Paste any cURL from this file.
5. Click `Continue` -> `Import`.
