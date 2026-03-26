#!/bin/bash

BASE_URL="http://localhost:8080/api/v1"
SEEKER_PHONE="+94711111111"
PROVIDER_PHONE="+94722222222"

register_user() {
  local phone=$1
  local name=$2
  local display_name=$3
  
  echo "Logging in/Registering $name ($phone)..." >&2
  # Init
  curl -s -X POST "$BASE_URL/auth/phone/init" -H "Content-Type: application/json" -d "{\"phoneNumber\": \"$phone\"}" > /dev/null
  sleep 2
  
  # Get OTP from logs
  local otp=$(docker logs --tail 50 helphub-app 2>&1 | grep "Phone OTP for $phone" | tail -n 1 | awk '{print $NF}')
  echo "  $name OTP: $otp" >&2
  
  # Verify
  local verify_resp=$(curl -s -X POST "$BASE_URL/auth/phone/verify" -H "Content-Type: application/json" -d "{\"phoneNumber\": \"$phone\", \"otp\": \"$otp\"}")
  local reg_req=$(echo "$verify_resp" | jq -r '.data.registrationRequired')
  local token=$(echo "$verify_resp" | jq -r '.data.accessToken')
  
  if [ "$reg_req" == "true" ]; then
    echo "  Registration required for $name..." >&2
    local pending_token=$(echo "$verify_resp" | jq -r '.data.pendingToken')
    token=$(curl -s -X POST "$BASE_URL/auth/phone/complete-registration" -H "Content-Type: application/json" -d "{
      \"pendingToken\": \"$pending_token\",
      \"fullName\": \"$name\",
      \"displayName\": \"$display_name\",
      \"dateOfBirth\": \"1990-01-01\"
    }" | jq -r '.data.accessToken')
  fi
  echo "$token"
}

echo "Preparing accounts..."
SEEKER_TOKEN=$(register_user "$SEEKER_PHONE" "Test Seeker" "seeker123")
PROVIDER_TOKEN=$(register_user "$PROVIDER_PHONE" "Test Provider" "provider456")

if [ "$SEEKER_TOKEN" == "null" ] || [ "$PROVIDER_TOKEN" == "null" ]; then
  echo "Failed to obtain tokens. Exiting."
  exit 1
fi
echo "Tokens obtained successfully."

echo "Seeker creates a job..."
JOB_RESP=$(curl -s -X POST "$BASE_URL/jobs" -H "Authorization: Bearer $SEEKER_TOKEN" -H "Content-Type: application/json" -d '{
  "title": "OTP Test Job",
  "description": "Testing the new flow with a much longer description to satisfy constraints",
  "price": 500,
  "scheduledAt": "2026-03-27T10:00:00",
  "locationAddress": "Test Lane",
  "locationCoordinates": "POINT(79.0 6.0)",
  "jobType": "FIXED"
}')
JOB_ID=$(echo "$JOB_RESP" | jq -r '.data.id')

if [ "$JOB_ID" == "null" ] || [ -z "$JOB_ID" ]; then
  echo "Failed to create job. Response: $JOB_RESP"
  exit 1
fi
echo "Job ID: $JOB_ID"

echo "Provider accepts the job..."
curl -s -X POST "$BASE_URL/jobs/$JOB_ID/accept" -H "Authorization: Bearer $PROVIDER_TOKEN" | jq .

echo "Provider requests Start OTP..."
curl -s -X POST "$BASE_URL/jobs/$JOB_ID/start-otp" -H "Authorization: Bearer $PROVIDER_TOKEN" | jq .
sleep 2
JOB_OTP=$(docker logs --tail 50 helphub-app 2>&1 | grep "Job OTP for job $JOB_ID" | tail -n 1 | awk '{print $NF}')
echo "Job Start OTP: $JOB_OTP"

echo "Seeker verifies and starts the job..."
curl -s -X POST "$BASE_URL/jobs/$JOB_ID/verify-start" -H "Authorization: Bearer $SEEKER_TOKEN" -H "Content-Type: application/json" -d "{\"otpCode\": \"$JOB_OTP\"}" | jq .

echo "Waiting 5 seconds to simulate work..."
sleep 5

echo "Provider pauses the job..."
curl -s -X POST "$BASE_URL/jobs/$JOB_ID/pause" -H "Authorization: Bearer $PROVIDER_TOKEN" -H "Content-Type: application/json" -d '{"reason": "Need parts"}' | jq .

echo "Provider requests Resume OTP..."
curl -s -X POST "$BASE_URL/jobs/$JOB_ID/resume-otp" -H "Authorization: Bearer $PROVIDER_TOKEN" | jq .
sleep 2
RESUME_OTP=$(docker logs --tail 50 helphub-app 2>&1 | grep "Job OTP for job $JOB_ID" | tail -n 1 | awk '{print $NF}')
echo "Job Resume OTP: $RESUME_OTP"

echo "Seeker verifies and resumes the job..."
curl -s -X POST "$BASE_URL/jobs/$JOB_ID/verify-resume" -H "Authorization: Bearer $SEEKER_TOKEN" -H "Content-Type: application/json" -d "{\"otpCode\": \"$RESUME_OTP\"}" | jq .

echo "Waiting another 5 seconds..."
sleep 5

echo "Provider marks job as complete..."
curl -s -X POST "$BASE_URL/jobs/$JOB_ID/provider-complete" -H "Authorization: Bearer $PROVIDER_TOKEN" | jq .

echo "Seeker confirms completion..."
curl -s -X POST "$BASE_URL/jobs/$JOB_ID/complete" -H "Authorization: Bearer $SEEKER_TOKEN" | jq .

echo "Getting Time Summary..."
curl -s -X GET "$BASE_URL/jobs/$JOB_ID/time-summary" -H "Authorization: Bearer $SEEKER_TOKEN" | jq .
