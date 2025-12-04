# 📌 Fitness AI App 

---

## ⚙️ Overview

A production-ready **Spring Boot Microservices** fitness platform powered by **Kafka, Gemini AI, Keycloak OAuth2 - PKCE, Docker, and AWS**. The system delivers real-time fitness insights with scalable, fault-tolerant architecture.

---

## 🏗️ Architecture Overview

```
/ (root)
├── configserver/        # Centralized config management
├── eureka/              # Service discovery & registry
├── gateway/             # API Gateway (single entry point)
├── authservice/         # OAuth2-based auth with Keycloak
├── userservice/         # User accounts & profiles
├── activityservice/     # Workout/activity tracking
├── aiservice/           # AI engine (Kafka consumer + Gemini AI)
└── docker/              # Deployment setup (Docker/AWS)
```

---

## ⭐ Key Features

### Scalable Microservices Ecosystem
- User, Activity, AI, Auth, Gateway & Config services orchestrated via Eureka and centralized configuration.

### Real-Time Fitness Intelligence
- Kafka streams activity data → AI Service consumes → Gemini AI generates instant personalized recommendations.

### High Availability Deployment
- Dockerized services deployed on AWS EC2, achieving ~99% uptime.

### Enterprise-Grade Security
- End-to-end protection using Keycloak OAuth2 / OpenID Connect, applied on all services.

### Low-Latency Processing
- Optimized AI pipeline reduces recommendation latency by ~30%.

---

## 🔄 System Flow

### Client → API Gateway
- All incoming requests pass through the Gateway for routing and security checks.

### API Gateway → Keycloak (Auth Service)

- Access tokens are validated.

- Unauthorized requests are blocked before reaching any microservice.

### Gateway → Core Microservices

- **User Service**: Handles signup, login, profiles.
- **Activity Service**: Receives workout logs, steps, calories, and activity data.


### Activity Events → Kafka

- Activity Service publishes user activity events to Kafka topics in real time.

### Kafka → AI Service

- AI Service consumes activity events.
- Processes data using Gemini AI to generate personalized fitness recommendations.

### AI Service → Gateway → Client

- AI results are sent back through the Gateway.
- Users receive real-time insights and recommendations.

### Config Server & Eureka (Internal Flow)

- All services pull configuration from Config Server.
- Eureka handles service discovery, load balancing, and fault tolerance.

---