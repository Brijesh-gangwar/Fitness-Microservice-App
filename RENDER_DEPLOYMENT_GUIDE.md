# Deployment Guide for Fitness App Microservices on Render

This guide covers deploying your Spring Boot microservices architecture on Render.com.

---

## 🎯 Overview

Your architecture has multiple services:
- **Eureka Server** (Service Discovery) - Port 8761
- **Config Server** (Centralized Config) - Port 8888
- **API Gateway** (Entry Point) - Port 8080
- **User Service** - Port 8081
- **Activity Service** - Port 8082
- **AI Service** (Kafka Consumer) - Port 8083

**Key Challenges on Render:**
- Render doesn't support Eureka's peer-to-peer replication (only single instance)
- Kafka requires persistent storage/external service (use Upstash Redis or similar)
- Database needs to be externalized (use Render PostgreSQL add-on)

---

## 📋 Prerequisites

1. **Render Account** - Sign up at https://render.com
2. **GitHub Repository** - Push your code to GitHub
3. **External Services Setup:**
   - PostgreSQL database (Render add-on)
   - Kafka/Message Queue (Upstash Kafka or Redis)
   - Keycloak instance (self-hosted or cloud provider)

---

## 🚀 Step-by-Step Deployment

### Step 1: Prepare Your Project

#### 1.1 Update Spring Boot Version (Optional but Recommended)
Render supports Java 11, 17, and 21. Your project uses Java 24, which isn't officially supported.

**Change in each pom.xml:**
```xml
<java.version>17</java.version>
```

#### 1.2 Create Dockerfile for Each Service

Create `Dockerfile` in the root of each service (eureka, configserver, gateway, userservice, activityservice, aiservice):

**Example: eureka/Dockerfile**
```dockerfile
FROM maven:3.9-eclipse-temurin-17 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 1.3 Create .dockerignore Files
**Create in root and each service:**
```
.git
.gitignore
.idea
.vscode
target
*.log
node_modules
.env
```

#### 1.4 Update application.yml for Production

**For Eureka (eureka/src/main/resources/application.yml):**
```yaml
spring:
  application:
    name: eureka
  jpa:
    hibernate:
      ddl-auto: update

eureka:
  instance:
    hostname: ${EUREKA_HOSTNAME:localhost}
    preferIpAddress: false
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    response-cache-update-interval-ms: 0

server:
  port: ${PORT:8761}
```

**For Config Server (configserver/src/main/resources/application.yml):**
```yaml
spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/YOUR-USERNAME/Fitness-App-Microservices.git
          search-paths: configserver/src/main/resources/config
          default-label: main

server:
  port: ${PORT:8888}
```

**For Gateway (gateway/src/main/resources/application.yml):**
```yaml
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lowerCaseServiceId: true
  config:
    import: optional:configserver:${CONFIG_SERVER_URL:http://localhost:8888}
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI}

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://localhost:8761/eureka}
  instance:
    prefer-ip-address: true
    hostname: ${EUREKA_HOSTNAME:localhost}

server:
  port: ${PORT:8080}
```

**For Each Microservice (userservice/activityservice/aiservice):**
```yaml
spring:
  application:
    name: user-service  # or activity-service, ai-service
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/fitness_db}
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver
  config:
    import: optional:configserver:${CONFIG_SERVER_URL:http://localhost:8888}

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://localhost:8761/eureka}
  instance:
    prefer-ip-address: true
    hostname: ${EUREKA_HOSTNAME:localhost}

kafka:
  bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
  producer:
    key-serializer: org.apache.kafka.common.serialization.StringSerializer
    value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
  consumer:
    group-id: fitness-group
    key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
    properties:
      spring.json.trusted.packages: "*"

server:
  port: ${PORT:8081}
```

---

### Step 2: Push to GitHub

```bash
cd e:\springboot-projects\Fitness-App-Microservices
git add .
git commit -m "Prepare for Render deployment"
git push origin main
```

---

### Step 3: Set Up External Services

#### 3.1 Create PostgreSQL Database on Render

1. Go to Render Dashboard → "New +" → "PostgreSQL"
2. Choose name: `fitness-db`
3. Region: Choose closest to your users
4. Create & note the connection string
5. Run initial SQL scripts to create tables

#### 3.2 Set Up Kafka/Message Queue

**Option A: Use Upstash Kafka** (Recommended for serverless)
1. Go to https://upstash.com
2. Create Kafka cluster
3. Get bootstrap servers, username, password

**Option B: Use Render's Internal Communication**
- Skip external Kafka initially; use database polling

---

### Step 4: Create Render Services

#### 4.1 Deploy Eureka Server

1. Go to Render Dashboard → "New +" → "Web Service"
2. **Connect Repository:**
   - Select your GitHub repo
   - Choose branch: `main`

3. **Configuration:**
   - **Name:** `fitness-eureka`
   - **Runtime:** `Docker`
   - **Region:** Choose your region
   - **Branch:** `main`

4. **Build Command:**
   ```
   cd eureka && mvn clean package -DskipTests
   ```

5. **Start Command:**
   ```
   java -jar eureka/target/*.jar
   ```

6. **Environment Variables:**
   ```
   PORT=10000
   EUREKA_HOSTNAME=fitness-eureka.onrender.com
   ```

7. Click "Create Web Service"

8. **Wait for deployment** (5-10 minutes). Get the URL: `https://fitness-eureka.onrender.com`

---

#### 4.2 Deploy Config Server

1. "New +" → "Web Service"
2. **Configuration:**
   - **Name:** `fitness-config-server`
   - **Runtime:** `Docker`
   
3. **Build & Start Commands:**
   ```
   Build: cd configserver && mvn clean package -DskipTests
   Start: java -jar configserver/target/*.jar
   ```

4. **Environment Variables:**
   ```
   PORT=10001
   ```

5. Click "Create Web Service"

6. Get the URL: `https://fitness-config-server.onrender.com`

---

#### 4.3 Deploy API Gateway

1. "New +" → "Web Service"
2. **Configuration:**
   - **Name:** `fitness-api-gateway`
   - **Runtime:** `Docker`

3. **Build & Start Commands:**
   ```
   Build: cd gateway && mvn clean package -DskipTests
   Start: java -jar gateway/target/*.jar
   ```

4. **Environment Variables:**
   ```
   PORT=10002
   EUREKA_URL=https://fitness-eureka.onrender.com/eureka
   CONFIG_SERVER_URL=https://fitness-config-server.onrender.com
   KEYCLOAK_ISSUER_URI=https://your-keycloak-instance/auth/realms/fitness
   ```

5. Click "Create Web Service"

---

#### 4.4 Deploy Microservices (User, Activity, AI)

Repeat for each service:

**For User Service:**
1. "New +" → "Web Service"
2. **Name:** `fitness-user-service`
3. **Build Command:** `cd userservice && mvn clean package -DskipTests`
4. **Start Command:** `java -jar userservice/target/*.jar`
5. **Environment Variables:**
   ```
   PORT=10003
   EUREKA_URL=https://fitness-eureka.onrender.com/eureka
   CONFIG_SERVER_URL=https://fitness-config-server.onrender.com
   DB_URL=postgresql://user:password@host:5432/fitness_db
   DB_USER=fitness_user
   DB_PASSWORD=your_password
   ```

**For Activity Service:**
- **Name:** `fitness-activity-service`
- **Build:** `cd activityservice && mvn clean package -DskipTests`
- **Start:** `java -jar activityservice/target/*.jar`
- **Env:** Same as User Service + Kafka details

**For AI Service:**
- **Name:** `fitness-ai-service`
- **Build:** `cd aiservice && mvn clean package -DskipTests`
- **Start:** `java -jar aiservice/target/*.jar`
- **Env:** Same as others + `GEMINI_API_KEY=your_key`

---

### Step 5: Configure Service Communication

#### 5.1 Update Eureka Service URLs

After all services are deployed, update the Eureka configuration for each service:

**Edit configserver/src/main/resources/config/user-service.yml:**
```yaml
eureka:
  instance:
    hostname: fitness-user-service.onrender.com
    prefer-ip-address: false
```

Do this for all config files.

---

### Step 6: Set Up Database

#### 6.1 Connect to PostgreSQL

Use Render's PostgreSQL credentials to run migrations:

```bash
# Using psql or database client
psql postgresql://user:password@host:5432/fitness_db < schema.sql
```

#### 6.2 Create Tables

Execute SQL scripts to create tables for users, activities, etc.

---

### Step 7: Set Up Kafka (If Not Using Database Polling)

If using Upstash Kafka:

1. Get bootstrap servers from Upstash
2. Update environment variables in all services:
   ```
   KAFKA_BOOTSTRAP_SERVERS=your-cluster.upstash.io:9092
   KAFKA_SASL_JAAS_CONFIG=org.apache.kafka.common.security.scram.ScramLoginModule required username="key" password="secret";
   KAFKA_SASL_MECHANISM=SCRAM-SHA-256
   KAFKA_SECURITY_PROTOCOL=SASL_SSL
   ```

---

### Step 8: Deploy & Monitor

1. **Verify Deployments:**
   - Check Render Dashboard for all services
   - Verify they have "Live" status
   - Check logs for errors

2. **Test Endpoints:**
   ```bash
   curl https://fitness-api-gateway.onrender.com/health
   curl https://fitness-eureka.onrender.com/eureka/apps
   ```

3. **Monitor Logs:**
   - Click on each service in Render Dashboard
   - View "Logs" section for errors

---

## 🔧 Troubleshooting

### Service Can't Find Eureka
- Verify `EUREKA_URL` environment variable is correct
- Check Eureka logs: `onrender.com` Logs tab
- Ensure service name in `spring.application.name` matches config

### Database Connection Fails
- Verify `DB_URL`, `DB_USER`, `DB_PASSWORD` are correct
- Check PostgreSQL service is "Live"
- Run `SELECT 1;` to test connection

### Kafka Connection Issues
- Verify bootstrap servers are correct
- Check SASL credentials if using authentication
- Look for timeout errors in logs

### Gateway Routing Fails
- Verify all services registered in Eureka
- Check gateway configuration routes
- Monitor gateway logs

---

## 📊 Monitoring & Scaling

### Enable Auto-Scaling on Render
1. Service Settings → "Auto-Scaling"
2. Set min/max instances based on traffic
3. Choose scaling metric (CPU, Memory)

### View Metrics
- Render Dashboard → Service → "Metrics"
- Monitor CPU, Memory, Requests

### Logs
- Service → "Logs" tab
- Use for debugging and monitoring

---

## 🔐 Security Checklist

- [ ] All services use HTTPS
- [ ] Database passwords are in environment variables
- [ ] Keycloak OAuth2 is configured
- [ ] API Gateway validates tokens
- [ ] Sensitive data not in code/logs
- [ ] Database backups enabled

---

## 📌 Important Notes

1. **Eureka Limitations on Render:**
   - Works with single instance only
   - In production, consider replacing with Spring Cloud Load Balancer

2. **Kafka:**
   - Render doesn't provide managed Kafka
   - Use Upstash or external provider
   - Alternatively, use database polling instead

3. **Cost Optimization:**
   - Use Render's free tier for testing
   - Monitor service usage
   - Consider combining smaller services

4. **Cold Starts:**
   - Java services have ~30-60 second startup time
   - Use Render's "Always On" for critical services (paid)

---

## ✅ Final Checklist

- [ ] All services deployed and "Live"
- [ ] Database created and migrated
- [ ] Kafka/Message queue configured
- [ ] Environment variables set correctly
- [ ] Eureka shows all services registered
- [ ] Gateway can route requests
- [ ] OAuth2/Keycloak working
- [ ] Logs show no errors
- [ ] Test endpoints working
- [ ] Monitoring and alerts configured

---

## 📞 Support

For issues:
1. Check Render Logs
2. Verify environment variables
3. Check service health endpoints
4. Review application logs locally with same config

