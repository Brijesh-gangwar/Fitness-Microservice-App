# Quick Deployment Checklist for Render

## Pre-Deployment

- [ ] All code pushed to GitHub main branch
- [ ] Render account created (https://render.com)
- [ ] GitHub account connected to Render

## External Services

- [ ] PostgreSQL instance created on Render
  - Note database URL: `_________________`
  - Note username: `_________________`
  - Note password: `_________________`

- [ ] Kafka cluster created (Upstash or similar)
  - Note bootstrap servers: `_________________`
  - Note username: `_________________`
  - Note password: `_________________`

- [ ] Keycloak instance available
  - Note issuer URI: `_________________`
  - Note client credentials: `_________________`

## Deployment Order

### 1. Eureka Service
- [ ] Create Web Service on Render
- [ ] Name: `fitness-eureka`
- [ ] Build Command: `cd eureka && mvn clean package -DskipTests`
- [ ] Start Command: `java -jar eureka/target/*.jar`
- [ ] Set environment variables
- [ ] Wait for "Live" status
- [ ] Note URL: `_________________`

### 2. Config Server
- [ ] Create Web Service on Render
- [ ] Name: `fitness-config-server`
- [ ] Build Command: `cd configserver && mvn clean package -DskipTests`
- [ ] Start Command: `java -jar configserver/target/*.jar`
- [ ] Set environment variables
- [ ] Wait for "Live" status
- [ ] Note URL: `_________________`

### 3. API Gateway
- [ ] Create Web Service on Render
- [ ] Name: `fitness-api-gateway`
- [ ] Build Command: `cd gateway && mvn clean package -DskipTests`
- [ ] Start Command: `java -jar gateway/target/*.jar`
- [ ] Set environment variables:
  - `EUREKA_URL` = from step 1
  - `CONFIG_SERVER_URL` = from step 2
  - `KEYCLOAK_ISSUER_URI` = from Keycloak setup
- [ ] Wait for "Live" status
- [ ] Note URL: `_________________`

### 4. User Service
- [ ] Create Web Service on Render
- [ ] Name: `fitness-user-service`
- [ ] Build Command: `cd userservice && mvn clean package -DskipTests`
- [ ] Start Command: `java -jar userservice/target/*.jar`
- [ ] Set environment variables:
  - Database credentials
  - Eureka/Config URLs
- [ ] Wait for "Live" status

### 5. Activity Service
- [ ] Create Web Service on Render
- [ ] Name: `fitness-activity-service`
- [ ] Build Command: `cd activityservice && mvn clean package -DskipTests`
- [ ] Start Command: `java -jar activityservice/target/*.jar`
- [ ] Set environment variables:
  - Database credentials
  - Eureka/Config URLs
  - Kafka credentials
- [ ] Wait for "Live" status

### 6. AI Service
- [ ] Create Web Service on Render
- [ ] Name: `fitness-ai-service`
- [ ] Build Command: `cd aiservice && mvn clean package -DskipTests`
- [ ] Start Command: `java -jar aiservice/target/*.jar`
- [ ] Set environment variables:
  - Database credentials
  - Eureka/Config URLs
  - Kafka credentials
  - `GEMINI_API_KEY`
- [ ] Wait for "Live" status

## Post-Deployment

- [ ] Check all services show "Live" status
- [ ] View logs for each service (check for errors)
- [ ] Test Eureka dashboard: `https://fitness-eureka.onrender.com/eureka/apps`
- [ ] Test Gateway health: `curl https://fitness-api-gateway.onrender.com/health`
- [ ] Test user service login endpoint
- [ ] Test activity logging endpoint
- [ ] Test AI recommendations endpoint
- [ ] Monitor resource usage in Render dashboard
- [ ] Set up alert notifications (optional)

## Troubleshooting

### Service shows "Failed" status
- [ ] Check build logs for compilation errors
- [ ] Verify Dockerfile exists and is correct
- [ ] Check Java version compatibility (Java 17 required)

### Service can't connect to Eureka
- [ ] Verify `EUREKA_URL` is correct in environment variables
- [ ] Check Eureka service is "Live"
- [ ] Review service logs in Render dashboard

### Database connection timeout
- [ ] Verify `DB_URL`, `DB_USER`, `DB_PASSWORD` are correct
- [ ] Check PostgreSQL service is running
- [ ] Ensure database exists and tables are created

### Kafka connection fails
- [ ] Verify `KAFKA_BOOTSTRAP_SERVERS` is correct
- [ ] Check Kafka cluster is running (if self-hosted)
- [ ] Verify SASL credentials if using authentication

## Monitoring

- [ ] Enable auto-scaling for high-traffic services
- [ ] Set up monitoring dashboards
- [ ] Configure email alerts for service failures
- [ ] Monitor database performance and growth
- [ ] Review logs regularly for errors

## Estimated Timeline

- Pre-deployment setup: 30-60 minutes
- Deploy Eureka & Config: 10-15 minutes
- Deploy Gateway: 10-15 minutes
- Deploy Microservices: 30-45 minutes
- Testing & troubleshooting: 30-60 minutes

**Total: 2-3 hours**
