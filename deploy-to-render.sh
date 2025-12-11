#!/bin/bash
# Render Deployment Script
# This script helps deploy all services to Render

set -e

echo "================================"
echo "Fitness App - Render Deployment"
echo "================================"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}Prerequisites:${NC}"
echo "1. GitHub repository pushed with all code"
echo "2. Render account created"
echo "3. PostgreSQL database created"
echo "4. Kafka cluster configured (Upstash or similar)"
echo ""

read -p "Continue with deployment? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    exit 1
fi

echo -e "${BLUE}Step 1: Verify GitHub push${NC}"
git status
read -p "Is everything committed and pushed? (y/n) " -n 1 -r
echo

echo -e "${BLUE}Step 2: Document your external services${NC}"
read -p "Enter your PostgreSQL connection string: " DB_URL
read -p "Enter your Kafka bootstrap servers: " KAFKA_SERVERS
read -p "Enter your Eureka service URL (leave blank for http://localhost:8761): " EUREKA_URL
EUREKA_URL=${EUREKA_URL:-"http://localhost:8761/eureka"}

echo -e "${BLUE}Step 3: Create services on Render${NC}"
echo ""
echo "Services to create (in order):"
echo "1. fitness-eureka (Web Service)"
echo "2. fitness-config-server (Web Service)"
echo "3. fitness-api-gateway (Web Service)"
echo "4. fitness-user-service (Web Service)"
echo "5. fitness-activity-service (Web Service)"
echo "6. fitness-ai-service (Web Service)"
echo ""

echo -e "${GREEN}Go to https://render.com and create each service.${NC}"
echo ""
echo "For each service:"
echo "  - Set Runtime to 'Docker'"
echo "  - Set Branch to 'main'"
echo "  - Add environment variables (see RENDER_ENV_VARIABLES.md)"
echo "  - Click 'Create Web Service'"
echo ""

echo -e "${BLUE}Waiting for services to be deployed...${NC}"
echo "This typically takes 5-10 minutes per service."
echo ""
echo -e "${GREEN}Deployment guide: See RENDER_DEPLOYMENT_GUIDE.md${NC}"
