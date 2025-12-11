# Render Deployment - Quick Reference

## Service Deployment Order & Build Commands

### 1️⃣ Eureka Server (Service Registry)
```
Name: fitness-eureka
Build: cd eureka && mvn clean package -DskipTests
Start: java -jar eureka/target/*.jar
Port: 8761
```

### 2️⃣ Config Server
```
Name: fitness-config-server
Build: cd configserver && mvn clean package -DskipTests
Start: java -jar configserver/target/*.jar
Port: 8888
```

### 3️⃣ API Gateway
```
Name: fitness-api-gateway
Build: cd gateway && mvn clean package -DskipTests
Start: java -jar gateway/target/*.jar
Port: 8080
```

### 4️⃣ User Service
```
Name: fitness-user-service
Build: cd userservice && mvn clean package -DskipTests
Start: java -jar userservice/target/*.jar
Port: 8081
```

### 5️⃣ Activity Service
```
Name: fitness-activity-service
Build: cd activityservice && mvn clean package -DskipTests
Start: java -jar activityservice/target/*.jar
Port: 8082
```

### 6️⃣ AI Service
```
Name: fitness-ai-service
Build: cd aiservice && mvn clean package -DskipTests
Start: java -jar aiservice/target/*.jar
Port: 8083
```

---

## Essential Environment Variables

### All Services Need:
```
PORT=10000 (unique for each)
EUREKA_HOSTNAME=service-name.onrender.com
EUREKA_URL=https://fitness-eureka.onrender.com/eureka
CONFIG_SERVER_URL=https://fitness-config-server.onrender.com
```

### Database Services Need:
```
DB_URL=postgresql://user:pass@host:5432/db
DB_USER=your_username
DB_PASSWORD=your_password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### Kafka Services Need:
```
KAFKA_BOOTSTRAP_SERVERS=your-cluster.upstash.io:9092
KAFKA_SASL_JAAS_CONFIG=org.apache.kafka.common.security.scram.ScramLoginModule required username="..." password="...";
KAFKA_SASL_MECHANISM=SCRAM-SHA-256
KAFKA_SECURITY_PROTOCOL=SASL_SSL
```

### Gateway Needs:
```
KEYCLOAK_ISSUER_URI=https://your-keycloak/auth/realms/fitness
```

### AI Service Needs:
```
GEMINI_API_KEY=your-api-key
```

---

## External Services Setup

### PostgreSQL
- Create on Render: Dashboard → "New +" → "PostgreSQL"
- Note connection string from dashboard
- Create database tables (run SQL migrations)

### Kafka
- Use Upstash: https://upstash.com/kafka
- Create cluster, get bootstrap servers & credentials

### Keycloak
- Can be self-hosted or use managed service
- Get issuer URI and client credentials

---

## Step-by-Step for Each Service

1. Go to https://render.com
2. Click "New +" → "Web Service"
3. Select GitHub repository
4. Fill in service details:
   - **Name:** (from table above)
   - **Runtime:** Docker
   - **Build Command:** (from table above)
   - **Start Command:** (from table above)
5. Add environment variables
6. Click "Create Web Service"
7. Wait for build to complete (5-10 min)
8. Check logs for errors

---

## Verify Deployment

After all services are deployed:

```bash
# Check Eureka dashboard
curl https://fitness-eureka.onrender.com/eureka/apps

# Check Gateway health
curl https://fitness-api-gateway.onrender.com/health

# Check if services registered
# Visit: https://fitness-eureka.onrender.com/
# Should see all 5 microservices in instances
```

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Service fails to build | Check pom.xml, Java version (use 17) |
| Can't connect to Eureka | Verify `EUREKA_URL` env var |
| Database connection error | Check `DB_URL`, `DB_USER`, `DB_PASSWORD` |
| Kafka timeout | Verify bootstrap servers & SASL credentials |
| Gateway returns 503 | Services not registered in Eureka, check logs |

---

## Monitoring

1. **View Logs:** Click service → "Logs" tab
2. **Check Metrics:** Service → "Metrics" tab
3. **Monitor CPU/Memory:** Watch for spikes
4. **Set Alerts:** Service Settings → Add notification

---

## Important Notes

- ✅ Eureka works but doesn't support clustering on Render
- ✅ Spring Cloud Config works with native profiles
- ✅ Gateway auto-discovers services via Eureka
- ⚠️ Java startup takes 30-60 seconds (cold starts)
- ⚠️ Free tier auto-spins down if unused for 15 minutes
- 💡 Use "Always On" for production services (paid)

---

## Useful Links

- [Render Documentation](https://render.com/docs)
- [Spring Boot on Render](https://render.com/docs/deploy-java)
- [Upstash Kafka](https://upstash.com/kafka)
- [Spring Cloud Config](https://spring.io/projects/spring-cloud-config)
- [Spring Cloud Netflix Eureka](https://spring.io/projects/spring-cloud-netflix)
