# Render Deployment - Troubleshooting & FAQ

## Common Issues & Solutions

### 🔴 Service Status: "Failed"

**Problem:** Service shows "Failed" or "Build Failed"

**Solutions:**
1. **Check Build Logs:**
   - Click on service → "Logs" tab
   - Look for compilation errors
   - Check if Maven command failed

2. **Verify Java Version:**
   - Ensure `pom.xml` has `<java.version>17</java.version>`
   - Java 24 is not supported on Render
   - Update all pom.xml files

3. **Check Dockerfile:**
   - Verify Dockerfile exists in service directory
   - Ensure correct JAR path in `COPY` command
   - Check `EXPOSE` port matches configuration

4. **Verify Dependencies:**
   ```bash
   mvn clean dependency:resolve
   ```

---

### 🔴 Service Starts But Then Crashes

**Problem:** Service briefly starts then shows "Failed"

**Solutions:**
1. **Check Application Logs:**
   - Click service → "Logs" tab
   - Look for `Exception` or `Error` lines
   - Common issues:
     - Database connection timeout
     - Cannot find Eureka server
     - Cannot find Config server

2. **Verify Environment Variables:**
   - Check all required env vars are set
   - Verify no typos in variable names
   - Try with simple values first

3. **Test Locally First:**
   ```bash
   # Set env vars and run locally
   $env:DB_URL="postgresql://..."
   $env:EUREKA_URL="http://localhost:8761/eureka"
   mvn spring-boot:run
   ```

---

### 🟠 Services Can't Connect to Eureka

**Problem:** Logs show "Cannot connect to Eureka"

**Checklist:**
- [ ] Is Eureka service "Live"?
- [ ] Is `EUREKA_URL` set correctly in all services?
- [ ] URL should be: `https://fitness-eureka.onrender.com/eureka`
- [ ] Not: `http://` (must be https)
- [ ] Check firewall/network permissions

**Fix:**
1. Go to Eureka service settings
2. Copy exact URL: `https://fitness-eureka.onrender.com/eureka`
3. Add to each service:
   ```
   EUREKA_URL=https://fitness-eureka.onrender.com/eureka
   ```
4. Restart each service (trigger redeploy)

---

### 🟠 Database Connection Timeout

**Problem:** "Connection timeout to database" in logs

**Solutions:**
1. **Verify PostgreSQL is Running:**
   - Go to Render Dashboard
   - Check if PostgreSQL service shows "Available"

2. **Check Connection String:**
   ```
   Format: postgresql://username:password@host:port/database_name
   
   Example: postgresql://user:mypass123@dpg-xyz.onrender.com:5432/fitness_db
   ```

3. **Verify Credentials:**
   - Username matches exactly
   - Password has no special characters (or URL-encode them)
   - Database name exists

4. **Test Connection Locally:**
   ```powershell
   # Test with psql
   psql "postgresql://user:password@host:5432/database_name" -c "SELECT 1;"
   ```

5. **Common Fix:**
   - PostgreSQL may auto-spin down after inactivity
   - Make a database query to wake it up
   - Check Render dashboard for "Spinning down" message

---

### 🟠 Kafka Connection Issues

**Problem:** "Failed to connect to Kafka bootstrap servers"

**Solutions:**
1. **Verify Kafka is Running:**
   - Check Upstash dashboard if using external Kafka
   - Verify cluster is active

2. **Check Bootstrap Servers Format:**
   ```
   Correct: your-cluster.upstash.io:9092
   Wrong: https://your-cluster.upstash.io:9092  (no protocol)
   ```

3. **Verify SASL Credentials:**
   ```
   KAFKA_SASL_JAAS_CONFIG must be set correctly:
   org.apache.kafka.common.security.scram.ScramLoginModule required 
     username="..." 
     password="...";
   ```

4. **Check SASL Mechanism:**
   ```
   For Upstash: KAFKA_SASL_MECHANISM=SCRAM-SHA-256
   For Upstash: KAFKA_SECURITY_PROTOCOL=SASL_SSL
   ```

5. **Test Connection:**
   - Use Upstash console to verify cluster is running
   - Check if topics exist
   - Verify credentials in Upstash dashboard

---

### 🟠 Gateway Returns 503 Service Unavailable

**Problem:** Gateway can't route to microservices

**Solutions:**
1. **Check if Services Registered in Eureka:**
   - Visit: `https://fitness-eureka.onrender.com/eureka/apps`
   - Look for all services listed
   - If not there, services haven't registered

2. **Why Services Might Not Register:**
   - Service can't reach Eureka (check `EUREKA_URL`)
   - Service crashed on startup (check logs)
   - Eureka server is down (check status)
   - Network connectivity issue

3. **Fix:**
   - Restart each service (trigger redeploy)
   - Monitor logs while starting
   - Wait 30-60 seconds for registration
   - Refresh Eureka dashboard

4. **Gateway Logs:**
   - Check gateway logs for routing errors
   - Look for "No instances available" messages
   - Verify service names in routes

---

### 🟠 High Memory or CPU Usage

**Problem:** Service using too much resources, might get terminated

**Solutions:**
1. **Add Memory Limits to application.yml:**
   ```yaml
   server:
     tomcat:
       max-threads: 10
   ```

2. **Enable Compression:**
   ```yaml
   server:
     compression:
       enabled: true
       min-response-size: 1024
   ```

3. **Reduce Log Level:**
   ```yaml
   logging:
     level:
       root: INFO  # Change from DEBUG
       org.springframework: WARN
   ```

4. **Upgrade Render Plan:**
   - Free tier has 512MB RAM
   - For Java microservices, consider paid tier
   - Or split services across multiple instances

---

### 🟠 Configuration Server Not Finding Configs

**Problem:** Services can't connect to Config Server or configs not loading

**Solutions:**
1. **Verify Config Server is Running:**
   - Check Render dashboard status
   - Try to access: `https://fitness-config-server.onrender.com`

2. **Check CONFIG_SERVER_URL:**
   - Should be: `https://fitness-config-server.onrender.com`
   - Not: `http://` (use https)

3. **Verify Config Files Exist:**
   - Check in `configserver/src/main/resources/config/`
   - Files should be named:
     - `user-service.yml`
     - `activity-service.yml`
     - `ai-service.yml`
     - etc.

4. **Test Config Server Locally:**
   ```bash
   mvn spring-boot:run
   # Visit: http://localhost:8888/user-service/default
   ```

5. **Check Application Name:**
   - Service's `spring.application.name` must match config file name
   - Example: If `spring.application.name=user-service`, need `user-service.yml`

---

## Frequently Asked Questions

### Q: How long does deployment take?
**A:** 
- Build: 3-5 minutes per service
- Startup: 30-60 seconds per Java service
- Registration: 10-30 seconds
- **Total: 5-10 minutes per service**

### Q: Can I use the free tier of Render?
**A:**
- ✅ Yes, but services spin down after 15 minutes of inactivity
- ✅ Good for development/testing
- ❌ Not recommended for production
- 💡 Use "Always On" for production (requires paid plan)

### Q: What's the cost of running this on Render?
**A:**
- PostgreSQL: ~$5-10/month
- Each service: ~$4/month (free tier) or $7/month (paid)
- 6 services × $7 = ~$42/month minimum
- Total: ~$50-60/month (with database)

### Q: How do I update code after deployment?
**A:**
1. Make changes locally
2. Commit and push to GitHub
3. Render automatically detects push
4. Service redeploys automatically
5. Takes 5-10 minutes

### Q: Can I scale individual services?
**A:**
- Render allows horizontal scaling (multiple instances)
- Enable "Auto-scaling" in service settings
- Set min/max instances based on traffic
- Scaling is available on paid plans

### Q: How do I monitor for errors?
**A:**
1. **View Logs:** Service → Logs tab (real-time)
2. **Check Metrics:** Service → Metrics tab (CPU, memory, requests)
3. **Set Alerts:** Service → Notifications (email on failure)
4. **Dashboard:** Main dashboard shows all service statuses

### Q: What if I need to use environment-specific configs?
**A:**
1. Create multiple application-*.yml files:
   - `application-dev.yml`
   - `application-prod.yml`
2. Set `SPRING_PROFILES_ACTIVE=prod` in env vars
3. Spring will load the correct profile

### Q: Can I access database from CLI?
**A:**
```powershell
# Install psql client
psql "postgresql://user:pass@host:5432/db"

# Or use Render's database editor
# Dashboard → PostgreSQL → Database → Open Editor
```

### Q: How do I backup my database?
**A:**
1. Render auto-backups daily
2. To manual backup:
   ```powershell
   pg_dump "postgresql://user:pass@host:5432/db" > backup.sql
   ```
3. Store backup safely

### Q: What if a service keeps crashing?
**A:**
1. Check logs first (most common: missing env vars)
2. Try with simplified config locally
3. Add health check endpoint
4. Gradually increase complexity
5. Contact Render support if infrastructure issue

---

## Performance Optimization Tips

### 1. Enable Caching
```yaml
spring:
  cache:
    type: simple
    cache-names:
      - users
      - activities
```

### 2. Connection Pooling
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
```

### 3. Lazy Loading
```yaml
spring:
  jpa:
    properties:
      hibernate:
        enable_lazy_load_no_trans: true
```

### 4. Compression
```yaml
server:
  compression:
    enabled: true
    min-response-size: 1024
```

---

## Security Best Practices

1. **Never commit secrets:** Use env vars for all credentials
2. **Use HTTPS:** All external URLs should use https://
3. **Enable CORS carefully:** Restrict to known domains
4. **Validate input:** Sanitize user inputs on gateway
5. **Database security:**
   - Use strong passwords
   - Restrict database access
   - Enable SSL connections
6. **Monitor logs:** Check for suspicious activity regularly

---

## Support Resources

- Render Docs: https://render.com/docs
- Spring Boot Docs: https://spring.io/projects/spring-boot
- Community: GitHub Discussions, Stack Overflow
- Email Support: Available for paid plans
