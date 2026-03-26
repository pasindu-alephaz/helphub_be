#!/bin/bash

# Configuration
BASE_URL="http://localhost:8080/api/v1"
ADMIN_EMAIL="admin@helphub.lk"
ADMIN_PASSWORD="admin"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== Admin Job Management API Test ===${NC}"

# 1. Login to get JWT Token
echo -e "\n${BLUE}1. Logging in as Admin...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/admin/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$ADMIN_EMAIL\", \"password\": \"$ADMIN_PASSWORD\"}")

TOKEN=$(echo $LOGIN_RESPONSE | grep -oP '"accessToken":"\K[^"]+')

if [ -z "$TOKEN" ]; then
  echo "Failed to get access token. Response: $LOGIN_RESPONSE"
  exit 1
fi

echo -e "${GREEN}Login successful! Token acquired.${NC}"

# 2. List all jobs (Section 8.1)
echo -e "\n${BLUE}2. Listing all jobs (Admin View)...${NC}"
curl -s -X GET "$BASE_URL/admin/jobs?page=0&size=5" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq .

# 3. Get specific job detail (Section 8.2)
# Using the ID found in earlier DB query
JOB_ID="99321f43-2e34-4ddc-aec9-46eaeb922ee0"

echo -e "\n${BLUE}3. Getting job detail for ID: $JOB_ID...${NC}"
curl -s -X GET "$BASE_URL/admin/jobs/$JOB_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq .

# 4. Update job details (Section 8.3)
echo -e "\n${BLUE}4. Updating job details (Admin Override)...${NC}"
curl -s -X PUT "$BASE_URL/admin/jobs/$JOB_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "preferredPrice": 5500.0,
    "jobPlan": "PREMIUM",
    "preferredLanguage": "English"
  }' | jq .

# 5. Force update status (Section 8.5)
echo -e "\n${BLUE}5. Force updating job status to COMPLETED...${NC}"
curl -s -X PUT "$BASE_URL/admin/jobs/$JOB_ID/status" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status": "COMPLETED"}' | jq .

# 7. List reported jobs (Section 9.1)
echo -e "\n${BLUE}7. Listing reported jobs (Admin View)...${NC}"
curl -s -X GET "$BASE_URL/admin/jobs/reports?page=0&size=5" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq .

# 8. Flag a job (Section 9.2)
echo -e "\n${BLUE}8. Flagging job ID: $JOB_ID...${NC}"
curl -s -X POST "$BASE_URL/admin/jobs/$JOB_ID/flag" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"reason": "Inappropriate content detected by admin"}' | jq .

# 9. List reported jobs again to see the flagged job
echo -e "\n${BLUE}9. Verifying job is in reports list...${NC}"
curl -s -X GET "$BASE_URL/admin/jobs/reports?page=0&size=5" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq .

# 10. Unflag a job (Section 9.3)
echo -e "\n${BLUE}10. Removing flag from job ID: $JOB_ID...${NC}"
curl -s -X POST "$BASE_URL/admin/jobs/$JOB_ID/unflag" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq .

# 11. Archive a job (Section 9.4)
echo -e "\n${BLUE}11. Archiving job ID: $JOB_ID...${NC}"
curl -s -X POST "$BASE_URL/admin/jobs/$JOB_ID/archive" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq .

# 12. User Reporting (User-facing endpoint)
# We need a user token for this, but for simplicity of this script let's reuse admin token 
# (assuming admin also has 'job_report' or just 'isAuthenticated' permission)
echo -e "\n${BLUE}12. Reporting job ID: $JOB_ID (as a user)...${NC}"
curl -s -X POST "$BASE_URL/jobs/$JOB_ID/report" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"reason": "Spam report from user"}' | jq .

# 13. Job Statistics (Section 10.1)
echo -e "\n${BLUE}13. Getting job statistics (Admin View)...${NC}"
curl -s -X GET "$BASE_URL/admin/jobs/stats" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq .

# 14. Popular Categories (Section 10.2)
echo -e "\n${BLUE}14. Getting popular categories (Admin View)...${NC}"
curl -s -X GET "$BASE_URL/admin/jobs/analytics/popular-categories?limit=5" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq .

echo -e "\n${GREEN}=== All Admin Job Tests Completed ===${NC}"

echo -e "\n${GREEN}=== Tests Completed ===${NC}"
