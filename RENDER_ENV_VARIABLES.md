# Render Environment Variables Template

## Copy these environment variables to each Render service

### EUREKA SERVICE
PORT=10000
EUREKA_HOSTNAME=fitness-eureka.onrender.com

### CONFIG SERVER
PORT=10001

### API GATEWAY
PORT=10002
EUREKA_URL=https://fitness-eureka.onrender.com/eureka
CONFIG_SERVER_URL=https://fitness-config-server.onrender.com
KEYCLOAK_ISSUER_URI=https://your-keycloak-instance/auth/realms/fitness

### USER SERVICE
PORT=10003
EUREKA_URL=https://fitness-eureka.onrender.com/eureka
CONFIG_SERVER_URL=https://fitness-config-server.onrender.com
DB_URL=postgresql://user:password@host:5432/fitness_db
DB_USER=fitness_user
DB_PASSWORD=your_secure_password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false

### ACTIVITY SERVICE
PORT=10004
EUREKA_URL=https://fitness-eureka.onrender.com/eureka
CONFIG_SERVER_URL=https://fitness-config-server.onrender.com
DB_URL=postgresql://user:password@host:5432/fitness_db
DB_USER=fitness_user
DB_PASSWORD=your_secure_password
KAFKA_BOOTSTRAP_SERVERS=your-kafka-cluster.upstash.io:9092
KAFKA_SASL_JAAS_CONFIG=org.apache.kafka.common.security.scram.ScramLoginModule required username="your_username" password="your_password";
KAFKA_SASL_MECHANISM=SCRAM-SHA-256
KAFKA_SECURITY_PROTOCOL=SASL_SSL

### AI SERVICE
PORT=10005
EUREKA_URL=https://fitness-eureka.onrender.com/eureka
CONFIG_SERVER_URL=https://fitness-config-server.onrender.com
DB_URL=postgresql://user:password@host:5432/fitness_db
DB_USER=fitness_user
DB_PASSWORD=your_secure_password
KAFKA_BOOTSTRAP_SERVERS=your-kafka-cluster.upstash.io:9092
KAFKA_SASL_JAAS_CONFIG=org.apache.kafka.common.security.scram.ScramLoginModule required username="your_username" password="your_password";
KAFKA_SASL_MECHANISM=SCRAM-SHA-256
KAFKA_SECURITY_PROTOCOL=SASL_SSL
GEMINI_API_KEY=your-gemini-api-key
