# Jobs API Specification

This document outlines the missing API endpoints needed for a complete Jobs section in HelpHub.

---

## Table of Contents

1. [Job Discovery & Retrieval](#1-job-discovery--retrieval)
2. [User's Own Jobs Management](#2-users-own-jobs-management)
3. [Job Workflow Actions](#3-job-workflow-actions)
4. [Job Bidding & Negotiation](#4-job-bidding--negotiation)
5. [Job Templates Management](#5-job-templates-management)
6. [Job Images Management](#6-job-images-management)
7. [Job Feedback & Reviews](#7-job-feedback--reviews)
8. [Admin Job Management](#8-admin-job-management)
9. [Admin Job Moderation](#9-admin-job-moderation)
10. [Admin Job Analytics](#10-admin-job-analytics)

---

## 1. Job Discovery & Retrieval

### GET /api/v1/jobs

**Purpose:** List and filter jobs with pagination and various filters.

```java
@GetMapping
public ResponseEntity<ApiResponse<Page<JobResponse>>> getJobs(
    @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
    @RequestParam(required = false) UUID subcategoryId,
    @RequestParam(required = false) String status,          // OPEN, IN_PROGRESS, COMPLETED, CANCELLED
    @RequestParam(required = false) String urgencyFlag,       // Normal, Urgent
    @RequestParam(required = false) BigDecimal minPrice,
    @RequestParam(required = false) BigDecimal maxPrice,
    @RequestParam(required = false) String locationCity,      // Filter by city
    @RequestParam(required = false) String jobType            // FIXED, BIDDING
) {
    // Returns paginated list of jobs matching filters
}
```

**Use Case:** Users browse available jobs in the marketplace, filter by category, price range, urgency, etc.

---

### GET /api/v1/jobs/{id}

**Purpose:** Get detailed information about a specific job.

```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable UUID id) {
    // Returns full job details including images, poster info, etc.
}
```

**Use Case:** Users view full details of a job before accepting or contacting the poster.

---

### GET /api/v1/jobs/nearby

**Purpose:** Find jobs near a specific location using geolocation.

```java
@GetMapping("/nearby")
public ResponseEntity<ApiResponse<List<JobResponse>>> getNearbyJobs(
    @RequestParam String coordinates,    // POINT(lat lon) format
    @RequestParam(defaultValue = "10") double radiusKm,
    @RequestParam(required = false) UUID subcategoryId
) {
    // Returns jobs within specified radius of coordinates
}
```

**Use Case:** Service providers find jobs in their local area.

---

## 2. User's Own Jobs Management

### GET /api/v1/jobs/my-jobs

**Purpose:** Get all jobs posted by the authenticated user.

```java
@GetMapping("/my-jobs")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<Page<JobResponse>>> getMyPostedJobs(
    Principal principal,
    @PageableDefault(size = 20) Pageable pageable,
    @RequestParam(required = false) String status
) {
    // Returns jobs posted by the current user
}
```

**Use Case:** Users manage and track the jobs they have posted.

---

### GET /api/v1/jobs/accepted

**Purpose:** Get jobs accepted by the authenticated user (for service providers).

```java
@GetMapping("/accepted")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<Page<JobResponse>>> getAcceptedJobs(
    Principal principal,
    @PageableDefault(size = 20) Pageable pageable,
    @RequestParam(required = false) String status
) {
    // Returns jobs the current user has accepted
}
```

**Use Case:** Service providers track jobs they are working on.

---

### PUT /api/v1/jobs/{id}

**Purpose:** Update job details (only by the job poster).

```java
@PutMapping("/{id}")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<JobResponse>> updateJob(
    Principal principal,
    @PathVariable UUID id,
    @Valid @RequestBody JobUpdateRequest request // contains recurringType, jobDate, jobTime, preferredPrice, jobType
) {
    // Updates job - only allowed if job is still OPEN and user is the poster
}
```

**Use Case:** Job posters modify job details before anyone accepts it.

---

### DELETE /api/v1/jobs/{id}

**Purpose:** Soft-delete/cancel a job.

```java
@DeleteMapping("/{id}")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<Void>> deleteJob(
    Principal principal,
    @PathVariable UUID id
) {
    // Soft deletes the job - marks deleted_at timestamp
}
```

**Use Case:** Job posters cancel their job posting.

---

---

### POST /api/v1/jobs/{id}/recurring/stop

**Purpose:** Stop future occurrences of a recurring job.

```java
@PostMapping("/{id}/recurring/stop")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<Void>> stopRecurringJob(
    Principal principal,
    @PathVariable UUID id
) {
    // Cancels the schedule for automatic job creation
}
```

**Use Case:** User pauses or cancels a recurring cleaning service.

## 3. Job Workflow Actions

### POST /api/v1/jobs/{id}/accept

**Purpose:** Accept/claim a job as a service provider.

```java
@PostMapping("/{id}/accept")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<JobResponse>> acceptJob(
    Principal principal,
    @PathVariable UUID id
) {
    // Sets acceptedBy to current user, changes status to IN_PROGRESS
}
```

**Use Case:** Service provider accepts a job opportunity.

---

### POST /api/v1/jobs/{id}/provider-complete

**Purpose:** Provider marks job as completed.

```java
@PostMapping("/{id}/provider-complete")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<JobResponse>> providerCompleteJob(
    Principal principal,
    @PathVariable UUID id,
    @RequestBody(required = false) ProviderCompleteRequest request // optional remarks/images
) {
    // Changes status to PENDING_CONFIRMATION - waiting for user to confirm
}
```

**Use Case:** Service provider indicates they have finished the work.

---

### POST /api/v1/jobs/{id}/complete

**Purpose:** Mark job as completed (by job poster).

```java
@PostMapping("/{id}/complete")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<JobResponse>> completeJob(
    Principal principal,
    @PathVariable UUID id
) {
    // Changes status to COMPLETED - job is done and payment can be released
}
```

**Use Case:** Job poster confirms the job is completed satisfactorily.

---

### POST /api/v1/jobs/{id}/dispute

**Purpose:** Initiate a dispute for a job.

```java
@PostMapping("/{id}/dispute")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<JobResponse>> disputeJob(
    Principal principal,
    @PathVariable UUID id,
    @RequestBody DisputeJobRequest request // reason, evidence
) {
    // Changes status to DISPUTED - alerts admin
}
```

**Use Case:** Either user or provider raises an issue regarding the job.

---

### POST /api/v1/jobs/{id}/location

**Purpose:** Update provider location during active job (for transport/delivery).

```java
@PostMapping("/{id}/location")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<Void>> updateJobLocation(
    Principal principal,
    @PathVariable UUID id,
    @RequestBody LocationUpdateRequest request // lat, lng
) {
    // Updates live tracking location
}
```

**Use Case:** Provider broadcasts location while en route.

---

### GET /api/v1/jobs/{id}/location

**Purpose:** Get provider's live location.

```java
@GetMapping("/{id}/location")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<LocationResponse>> getJobLocation(
    Principal principal,
    @PathVariable UUID id
) {
    // Returns latest coordinates
}
```

**Use Case:** User tracks provider's arrival.

---

### POST /api/v1/jobs/{id}/cancel

**Purpose:** Cancel a job (before or during progress).

```java
@PostMapping("/{id}/cancel")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<JobResponse>> cancelJob(
    Principal principal,
    @PathVariable UUID id,
    @RequestBody(required = false) CancelJobRequest request  // optional reason
) {
    // Changes status to CANCELLED, clears acceptedBy
}
```

**Use Case:** Either party cancels the job with an optional reason.

---

### POST /api/v1/jobs/{id}/start

**Purpose:** Mark job as started (by the service provider).

```java
@PostMapping("/{id}/start")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<JobResponse>> startJob(
    Principal principal,
    @PathVariable UUID id
) {
    // Confirms work has begun, status remains IN_PROGRESS
}
```

**Use Case:** Service provider confirms they have started working on the job.

---

### POST /api/v1/jobs/{id}/reject

**Purpose:** Reject an accepted job (by service provider).

```java
@PostMapping("/{id}/reject")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<JobResponse>> rejectJob(
    Principal principal,
    @PathVariable UUID id,
    @RequestBody(required = false) RejectJobRequest request  // optional reason
) {
    // Clears acceptedBy, changes status back to OPEN
}
```

**Use Case:** Service provider decides not to proceed with the job.

---

## 4. Job Bidding & Negotiation

### POST /api/v1/jobs/{id}/bids

**Purpose:** Provider submits a bid for a job.

```java
@PostMapping("/{id}/bids")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<BidResponse>> submitBid(
    Principal principal,
    @PathVariable UUID id,
    @Valid @RequestBody BidRequest request
) {
    // Submits quote for a BIDDING type job
}
```

**Use Case:** Provider competes for a job by offering a price.

---

### PUT /api/v1/jobs/{id}/bids/{bidId}

**Purpose:** Provider adjusts their submitted bid.

```java
@PutMapping("/{id}/bids/{bidId}")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<BidResponse>> adjustBid(
    Principal principal,
    @PathVariable UUID id,
    @PathVariable UUID bidId,
    @Valid @RequestBody BidRequest request
) {
    // Modifies existing bid before it's accepted
}
```

**Use Case:** Provider negotiates or corrects quote after seeing more details.

---

### GET /api/v1/jobs/{id}/bids

**Purpose:** User views all bids for their job.

```java
@GetMapping("/{id}/bids")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<List<BidResponse>>> getJobBids(
    Principal principal,
    @PathVariable UUID id
) {
    // Returns list of bids sorted by amount, rating, or time
}
```

**Use Case:** Job poster evaluates proposals from different providers.

---

### POST /api/v1/jobs/{id}/bids/{bidId}/accept

**Purpose:** User accepts a specific bid.

```java
@PostMapping("/{id}/bids/{bidId}/accept")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<JobResponse>> acceptBid(
    Principal principal,
    @PathVariable UUID id,
    @PathVariable UUID bidId
) {
    // Assigns job to the winning provider, rejects others, status becomes IN_PROGRESS
}
```

**Use Case:** Job poster officially hires the provider.

---

### POST /api/v1/jobs/{id}/messages

**Purpose:** Send a negotiation/chat message.

```java
@PostMapping("/{id}/messages")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
    Principal principal,
    @PathVariable UUID id,
    @Valid @RequestBody MessageRequest request
) {
    // Appends message to job's chat history
}
```

**Use Case:** User and provider clarify requirements.

---

### GET /api/v1/jobs/{id}/messages

**Purpose:** Get negotiation/chat history.

```java
@GetMapping("/{id}/messages")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(
    Principal principal,
    @PathVariable UUID id
) {
    // Retrieves chat messages
}
```

**Use Case:** Load chat history for a job.

---

## 5. Job Templates Management

### GET /api/v1/jobs/templates

**Purpose:** List all templates created by the authenticated user.

```java
@GetMapping("/templates")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<List<JobTemplateResponse>>> getMyTemplates(
    Principal principal
) {
    // Returns all job templates for the current user
}
```

**Use Case:** Users see their saved job templates for quick posting.

---

### GET /api/v1/jobs/templates/{id}

**Purpose:** Get details of a specific template.

```java
@GetMapping("/templates/{id}")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<JobTemplateResponse>> getTemplateById(
    Principal principal,
    @PathVariable UUID id
) {
    // Returns template details
}
```

**Use Case:** Users view a template before using it to create a job.

---

### PUT /api/v1/jobs/templates/{id}

**Purpose:** Update a job template.

```java
@PutMapping("/templates/{id}")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<JobTemplateResponse>> updateTemplate(
    Principal principal,
    @PathVariable UUID id,
    @Valid @RequestBody JobTemplateUpdateRequest request
) {
    // Updates template details
}
```

**Use Case:** Users modify their saved templates.

---

### DELETE /api/v1/jobs/templates/{id}

**Purpose:** Delete a job template.

```java
@DeleteMapping("/templates/{id}")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<Void>> deleteTemplate(
    Principal principal,
    @PathVariable UUID id
) {
    // Permanently deletes the template
}
```

**Use Case:** Users remove templates they no longer need.

---

### POST /api/v1/jobs/templates/{id}/use

**Purpose:** Create a new job from an existing template.

```java
@PostMapping("/templates/{id}/use")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<JobResponse>> createJobFromTemplate(
    Principal principal,
    @PathVariable UUID id,
    @RequestBody(required = false) JobFromTemplateRequest request  // optional overrides
) {
    // Creates new job using template data, optionally overriding some fields
}
```

**Use Case:** Quickly post a recurring job using a saved template.

---

## 6. Job Images Management

### GET /api/v1/jobs/{id}/images

**Purpose:** Get all images attached to a job.

```java
@GetMapping("/{id}/images")
public ResponseEntity<ApiResponse<List<String>>> getJobImages(
    @PathVariable UUID id
) {
    // Returns list of image URLs for the job
}
```

**Use Case:** View all images related to a job.

---

### DELETE /api/v1/jobs/{id}/images/{imageId}

**Purpose:** Delete a specific image from a job.

```java
@DeleteMapping("/{id}/images/{imageId}")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<Void>> deleteJobImage(
    Principal principal,
    @PathVariable UUID id,
    @PathVariable UUID imageId
) {
    // Removes image from job - only allowed for job poster
}
```

**Use Case:** Job poster removes an uploaded image.

---

## 7. Job Feedback & Reviews

### POST /api/v1/jobs/{id}/reviews/provider

**Purpose:** User rates the provider after job completion.

```java
@PostMapping("/{id}/reviews/provider")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<ReviewResponse>> rateProvider(
    Principal principal,
    @PathVariable UUID id,
    @Valid @RequestBody ReviewRequest request // 1-5 stars, comment, media
) {
    // Submits feedback for provider
}
```

**Use Case:** User provides public rating indicating satisfaction with provider.

---

### POST /api/v1/jobs/{id}/reviews/user

**Purpose:** Provider rates the user after job completion.

```java
@PostMapping("/{id}/reviews/user")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ApiResponse<ReviewResponse>> rateUser(
    Principal principal,
    @PathVariable UUID id,
    @Valid @RequestBody ReviewRequest request // 1-5 stars, comment, media
) {
    // Submits feedback for user
}
```

**Use Case:** Provider leaves feedback on the customer.

---

## 8. Admin Job Management

Base path: `/api/v1/admin/jobs`

### GET /api/v1/admin/jobs

**Purpose:** List all jobs with extended filters (admin view).

```java
@GetMapping
@PreAuthorize("hasAuthority('job_read')")
public ResponseEntity<ApiResponse<Page<JobResponse>>> getAllJobsAdmin(
    @PageableDefault(size = 20) Pageable pageable,
    @RequestParam(required = false) UUID userId,         // Filter by poster
    @RequestParam(required = false) UUID providerId,     // Filter by accepted provider
    @RequestParam(required = false) String status,
    @RequestParam(required = false) UUID subcategoryId,
    @RequestParam(required = false) LocalDateTime fromDate,
    @RequestParam(required = false) LocalDateTime toDate
) {
    // Returns all jobs with admin-level filtering
}
```

**Use Case:** Admin reviews all jobs in the system.

---

### GET /api/v1/admin/jobs/{id}

**Purpose:** Get detailed job information (admin view).

```java
@GetMapping("/{id}")
@PreAuthorize("hasAuthority('job_read')")
public ResponseEntity<ApiResponse<JobResponse>> getJobByIdAdmin(
    @PathVariable UUID id
) {
    // Returns full job details with additional admin info
}
```

**Use Case:** Admin views complete job details for investigation.

---

### PUT /api/v1/admin/jobs/{id}

**Purpose:** Update any job details (admin override).

```java
@PutMapping("/{id}")
@PreAuthorize("hasAuthority('job_update')")
public ResponseEntity<ApiResponse<JobResponse>> updateJobAdmin(
    @PathVariable UUID id,
    @Valid @RequestBody JobUpdateRequest request // contains recurringType, jobDate, jobTime, preferredPrice, jobType
) {
    // Admin can modify any job field
}
```

**Use Case:** Admin corrects job information or resolves disputes.

---

### DELETE /api/v1/admin/jobs/{id}

**Purpose:** Permanently delete a job (admin hard delete).

```java
@DeleteMapping("/{id}")
@PreAuthorize("hasAuthority('job_delete')")
public ResponseEntity<ApiResponse<Void>> deleteJobAdmin(
    @PathVariable UUID id
) {
    // Hard delete - completely removes from database
}
```

**Use Case:** Admin removes inappropriate or spam job postings.

---

### PUT /api/v1/admin/jobs/{id}/status

**Purpose:** Force update job status (admin override).

```java
@PutMapping("/{id}/status")
@PreAuthorize("hasAuthority('job_update')")
public ResponseEntity<ApiResponse<JobResponse>> updateJobStatusAdmin(
    @PathVariable UUID id,
    @RequestParam String status
) {
    // Admin can set any status regardless of normal rules
}
```

**Use Case:** Admin manually resolves status disputes.

---

## 9. Admin Job Moderation

### GET /api/v1/admin/jobs/reports

**Purpose:** Get all reported or flagged jobs.

```java
@GetMapping("/reports")
@PreAuthorize("hasAuthority('job_read')")
public ResponseEntity<ApiResponse<Page<JobResponse>>> getReportedJobs(
    @PageableDefault(size = 20) Pageable pageable
) {
    // Returns jobs that have been flagged/reported by users
}
```

**Use Case:** Admin reviews jobs that users have reported.

---

### POST /api/v1/admin/jobs/{id}/flag

**Purpose:** Flag a job for review (admin action).

```java
@PostMapping("/{id}/flag")
@PreAuthorize("hasAuthority('job_update')")
public ResponseEntity<ApiResponse<JobResponse>> flagJob(
    @PathVariable UUID id,
    @RequestBody FlagJobRequest request  // reason for flagging
) {
    // Marks job for admin review
}
```

**Use Case:** Admin flags suspicious jobs for investigation.

---

### POST /api/v1/admin/jobs/{id}/unflag

**Purpose:** Remove flag from a job.

```java
@PostMapping("/{id}/unflag")
@PreAuthorize("hasAuthority('job_update')")
public ResponseEntity<ApiResponse<JobResponse>> unflagJob(
    @PathVariable UUID id
) {
    // Removes flag from job
}
```

**Use Case:** Admin clears flag after review.

---

### POST /api/v1/admin/jobs/{id}/archive

**Purpose:** Archive a completed job.

```java
@PostMapping("/{id}/archive")
@PreAuthorize("hasAuthority('job_update')")
public ResponseEntity<ApiResponse<JobResponse>> archiveJob(
    @PathVariable UUID id
) {
    // Moves job to archived status
}
```

**Use Case:** Admin archives old completed jobs for data management.

---

## 10. Admin Job Analytics

### GET /api/v1/admin/jobs/stats

**Purpose:** Get job statistics and counts.

```java
@GetMapping("/stats")
@PreAuthorize("hasAuthority('job_read')")
public ResponseEntity<ApiResponse<JobStatsResponse>> getJobStats(
    @RequestParam(required = false) LocalDateTime fromDate,
    @RequestParam(required = false) LocalDateTime toDate
) {
    // Returns counts: total jobs, by status, by urgency, etc.
}
```

**Response Example:**
```json
{
  "totalJobs": 1500,
  "openJobs": 200,
  "inProgressJobs": 150,
  "completedJobs": 1000,
  "cancelledJobs": 150,
  "urgentJobs": 300,
  "averagePrice": 2500.00
}
```

---

### GET /api/v1/admin/jobs/analytics/popular-categories

**Purpose:** Get most requested job categories.

```java
@GetMapping("/analytics/popular-categories")
@PreAuthorize("hasAuthority('job_read')")
public ResponseEntity<ApiResponse<List<CategoryStatsResponse>>> getPopularCategories(
    @RequestParam(defaultValue = "10") int limit
) {
    // Returns top categories by job count
}
```

**Use Case:** Business analytics to understand demand.

---

## Job Status Flow

```
OPEN ──(Bid Accepted/Claimed)──► IN_PROGRESS ──(Provider Completes)──► PENDING_CONFIRMATION ──(User Confirms)──► COMPLETED
  │                                   │                                         │
  │                                   ├──(Rejected)──► OPEN                     ├──(Dispute)──► DISPUTED
  │                                   │                                         │
  │                                   └──(Cancelled)──► CANCELLED               └──(Cancelled)──► CANCELLED
  │
  └──(Cancelled)──► CANCELLED
```

---

## Required Permissions

| Permission | Description |
|-------------|-------------|
| `job_read` | View jobs |
| `job_create` | Create jobs |
| `job_update` | Update own jobs / Admin override |
| `job_delete` | Delete own jobs / Admin hard delete |
| `job_accept` | Accept jobs as provider |
| `job_template_read` | View job templates |
| `job_template_create` | Create job templates |
| `job_template_update` | Update job templates |
| `job_template_delete` | Delete job templates |

---

## Notes

- All endpoints follow the standard response format defined in `ApiResponse.java`
- Pagination uses Spring's `Pageable` with default page size of 20
- Coordinates use PostGIS POINT format: `POINT(longitude latitude)`
- Soft deletes set the `deleted_at` timestamp rather than removing records
- Admin routes require specific role permissions as noted above
