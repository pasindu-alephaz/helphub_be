# User's Own Jobs Management API Implementation Plan

## Overview

This plan outlines the implementation of Section 2 ("User's Own Jobs Management") from [`docs/jobs_api_specification.md`](docs/jobs_api_specification.md).

## Excluded Features

The following features from the specification are **NOT** included in this plan:
- `POST /api/v1/jobs/{id}/recurring/stop` - Recurring job functionality

## APIs to Implement

| # | Endpoint | Method | Description |
|---|----------|--------|-------------|
| 1 | `/api/v1/jobs/my-jobs` | GET | Get all jobs posted by the authenticated user |
| 2 | `/api/v1/jobs/accepted` | GET | Get jobs accepted by the authenticated user (service provider view) |
| 3 | `/api/v1/jobs/{id}` | PUT | Update job details (only by job poster) |
| 4 | `/api/v1/jobs/{id}` | DELETE | Soft-delete/cancel a job |

## Implementation Details

### 1. Database Migration

**File:** `src/main/resources/db/changelog/changes/006-job-fields.sql`

Add missing columns to the `jobs` table:
- `job_type` - VARCHAR(20) - FIXED or BIDDING
- `preferred_price` - DECIMAL(10,2) - Similar to price field

```sql
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS job_type VARCHAR(20);
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS preferred_price DECIMAL(10,2);
```

### 2. Entity Updates

**File:** [`src/main/java/lk/helphub/api/domain/entity/Job.java`](src/main/java/lk/helphub/api/domain/entity/Job.java)

Add new fields:
```java
@Column(name = "job_type", length = 20)
private String jobType;

@Column(name = "preferred_price", precision = 10, scale = 2)
private BigDecimal preferredPrice;
```

### 3. DTOs

#### 3.1 JobUpdateRequest (New File)

**File:** [`src/main/java/lk/helphub/api/application/dto/JobUpdateRequest.java`](src/main/java/lk/helphub/api/application/dto/JobUpdateRequest.java)

Fields based on API spec:
- `jobType` - String (FIXED, BIDDING)
- `jobDate` - LocalDate (maps to scheduledAt)
- `jobTime` - LocalTime (maps to scheduledAt)
- `preferredPrice` - BigDecimal

#### 3.2 JobResponse Updates

**File:** [`src/main/java/lk/helphub/api/application/dto/JobResponse.java`](src/main/java/lk/helphub/api/application/dto/JobResponse.java)

Add new fields:
- `jobType`
- `preferredPrice`

### 4. Repository

**File:** [`src/main/java/lk/helphub/api/domain/repository/JobRepository.java`](src/main/java/lk/helphub/api/domain/repository/JobRepository.java)

Add query methods:
```java
Page<Job> findByPostedByEmailAndDeletedAtIsNull(String email, Pageable pageable);
Page<Job> findByPostedByEmailAndStatusAndDeletedAtIsNull(String email, String status, Pageable pageable);
Page<Job> findByAcceptedByEmailAndDeletedAtIsNull(String email, Pageable pageable);
Page<Job> findByAcceptedByEmailAndStatusAndDeletedAtIsNull(String email, String status, Pageable pageable);
Optional<Job> findByIdAndPostedByEmail(UUID id, String email);
```

### 5. Service Layer

#### 5.1 JobService Interface

**File:** [`src/main/java/lk/helphub/api/application/services/JobService.java`](src/main/java/lk/helphub/api/application/services/JobService.java)

Add methods:
```java
Page<JobResponse> getMyPostedJobs(String userEmail, Pageable pageable, String status);
Page<JobResponse> getAcceptedJobs(String userEmail, Pageable pageable, String status);
JobResponse updateJob(UUID jobId, String userEmail, JobUpdateRequest request);
void deleteJob(UUID jobId, String userEmail);
```

#### 5.2 JobServiceImpl

**File:** [`src/main/java/lk/helphub/api/application/services/impl/JobServiceImpl.java`](src/main/java/lk/helphub/api/application/services/impl/JobServiceImpl.java)

Implement new methods with authorization checks:
- Verify user is the job poster for update/delete
- Ensure job status is OPEN before allowing updates
- Implement soft delete by setting deletedAt timestamp

### 6. Controller Endpoints

**File:** [`src/main/java/lk/helphub/api/presentation/controller/JobController.java`](src/main/java/lk/helphub/api/presentation/controller/JobController.java)

Add endpoints:

#### GET /my-jobs
```java
@GetMapping("/my-jobs")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<Page<JobResponse>>> getMyPostedJobs(
    Principal principal,
    @PageableDefault(size = 20) Pageable pageable,
    @RequestParam(required = false) String status
)
```

#### GET /accepted
```java
@GetMapping("/accepted")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<Page<JobResponse>>> getAcceptedJobs(
    Principal principal,
    @PageableDefault(size = 20) Pageable pageable,
    @RequestParam(required = false) String status
)
```

#### PUT /{id}
```java
@PutMapping("/{id}")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<JobResponse>> updateJob(
    Principal principal,
    @PathVariable UUID id,
    @Valid @RequestBody JobUpdateRequest request
)
```

#### DELETE /{id}
```java
@DeleteMapping("/{id}")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<Void>> deleteJob(
    Principal principal,
    @PathVariable UUID id
)
```

### 7. Error Handling

Expected error scenarios:
- Job not found (404)
- User not authorized to update/delete job (403)
- Job status not OPEN for updates (400)
- Job already deleted (400)

Add custom exceptions or use existing `ResourceNotFoundException`.

### 8. Unit Tests

**File:** Create new test class `JobManagementControllerTest.java`

Test scenarios:
- Successful retrieval of user's posted jobs
- Successful retrieval of accepted jobs
- Successful job update by owner
- Unauthorized update attempt (403)
- Successful soft delete
- Job not found scenarios

## Implementation Sequence

1. Database migration (Liquibase)
2. Entity field additions
3. DTO creation/updates
4. Repository query methods
5. Service interface additions
6. Service implementation
7. Controller endpoints
8. Unit tests

## Notes

- All endpoints require authentication (`@PreAuthorize("isAuthenticated()")`)
- Authorization checks ensure users can only modify their own jobs
- Soft delete uses `deletedAt` timestamp rather than actual record deletion
- Pagination is supported using Spring's `Pageable`
