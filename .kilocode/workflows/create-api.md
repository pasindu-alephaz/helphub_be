---
description: How to create a new REST API endpoint following the HelpHub layered architecture
---

# Rules for Creating a New API

Follow these steps **in order**. Base package: `lk.helphub.api`.

---

## 1. Domain Layer — Entity

**Path:** `domain/models/{EntityName}.java`

- `@Entity`, `@Table(name = "table_name")`
- `@Id` + `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Jakarta Validation on fields (`@NotNull`, `@NotBlank`, `@Size`, etc.)
- `@Column` for nullability, uniqueness, length

```java
@Entity
@Table(name = "examples")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Example {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 255)
    @Column(nullable = false)
    private String name;
}
```

---

## 2. Domain Layer — Repository

**Path:** `domain/repositories/{EntityName}Repository.java`

- Extend `JpaRepository<EntityName, Long>`
- Add custom queries via naming conventions or `@Query`

```java
@Repository
public interface ExampleRepository extends JpaRepository<Example, Long> {
    Optional<Example> findByName(String name);
}
```

---

## 3. Application Layer — Custom Exceptions

**Path:** `application/exceptions/{ExceptionName}.java`

- Extend `RuntimeException` (e.g. `ResourceNotFoundException`, `DuplicateResourceException`)

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
```

---

## 4. Application Layer — Service

**Path:** `application/services/{EntityName}Service.java` (interface) + `{EntityName}ServiceImpl.java`

- Interface first, then `@Service` implementation
- Constructor injection via `@RequiredArgsConstructor`
- All business logic here — **never** in the controller
- Throw custom exceptions from this layer

```java
public interface ExampleService {
    Example create(Example dto);
    Example getById(Long id);
    List<Example> getAll();
    Example update(Long id, Example dto);
    void delete(Long id);
}
```

```java
@Service
@RequiredArgsConstructor
@Transactional
public class ExampleServiceImpl implements ExampleService {
    private final ExampleRepository exampleRepository;

    @Override
    public Example create(Example dto) {
        return exampleRepository.save(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public Example getById(Long id) {
        return exampleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Not found: " + id));
    }

    // getAll(), update(), delete() follow the same pattern
}
```

---

## 5. Presentation Layer — ApiResponse

**Path:** `presentation/dto/ApiResponse.java`

- We use a generic `ApiResponse<T>` wrapper for all responses.
- You generally do **not** need to create specific Request or Response DTOs unless the payload differs significantly from the Entity. Instead, pass the Entity class in the `@RequestBody` and return it wrapped in the `ApiResponse`.

```java
// Our generic ApiResponse wrapper looks roughly like this (already exists in the project):
@Getter @Setter @Builder
public class ApiResponse<T> {
    private boolean status;
    private String statusCode;
    private String message;
    private List<String> errors;
    private T data;
}
```

---

## 6. Presentation Layer — Controller

**Path:** `presentation/controllers/{EntityName}Controller.java`

- `@RestController` + `@RequestMapping("/api/v1/{plural-entity}")`
- Constructor injection via `@RequiredArgsConstructor`
- `@Valid` on request body parameters
- Return `ResponseEntity<>` with appropriate status codes
- **Security:** Use `@PreAuthorize("hasAuthority('entity_action')")` for all non-public endpoints.
    - Pattern: `entity_action` (e.g., `job_create`, `job_read`, `job_update`, `job_delete`).
    - Note: `PermissionSyncRunner` will automatically detect these and sync them to the database.

> After creating the controller, follow `/swagger-setup` to add OpenAPI annotations.

```java
@RestController
@RequestMapping("/api/v1/examples")
@RequiredArgsConstructor
public class ExampleController {
    private final ExampleService exampleService;

    @PostMapping
    @PreAuthorize("hasAuthority('example_create')")
    public ResponseEntity<ApiResponse<Example>> create(@Valid @RequestBody Example dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<Example>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Example created successfully")
                .data(exampleService.create(dto))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Example>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<Example>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Example retrieved successfully")
                .data(exampleService.getById(id))
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Example>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<Example>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Examples retrieved successfully")
                .data(exampleService.getAll())
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Example>> update(@PathVariable Long id, @Valid @RequestBody Example dto) {
        return ResponseEntity.ok(ApiResponse.<Example>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Example updated successfully")
                .data(exampleService.update(id, dto))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        exampleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## 7. Global Exception Handler

**Path:** `application/exceptions/GlobalExceptionHandler.java` — create once, project-wide.

- `@RestControllerAdvice`
- `ResourceNotFoundException` → 404
- `MethodArgumentNotValidException` → 400 with field errors
- Generic `Exception` → 500

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
```

---

## 8. Security — Permit the New Endpoint (if public)

**Path:** `infrastructure/security/SecurityConfig.java`

If the endpoint is public, add its path to `requestMatchers(...).permitAll()`.

**Authenticated endpoints:**
- Use `@PreAuthorize("hasAuthority('entity_action')")` on controller methods.
- The `PermissionSyncRunner` will automatically create the permission in the DB and assign it to the `ADMIN` role on startup.

---

## 9. Set Up Swagger Documentation

Follow the `/swagger-setup` workflow to add OpenAPI annotations to your controller and DTOs.

---

## 10. Create Tests

Create test files after each API is created to ensure the reliability of the new endpoints.
- Write unit tests for the Service layer (`application/services/`).
- Write integration tests for the Controller layer (`presentation/controllers/`).

---

## Quick Checklist

- [ ] `domain/models/{Entity}.java`
- [ ] `domain/repositories/{Entity}Repository.java`
- [ ] `application/exceptions/` — custom exceptions
- [ ] `application/services/{Entity}Service.java` + `ServiceImpl`
- [ ] `presentation/dto/ApiResponse.java` — ensure it exists
- [ ] `presentation/controllers/{Entity}Controller.java`
- [ ] `application/exceptions/GlobalExceptionHandler.java` — if missing
- [ ] `infrastructure/security/SecurityConfig.java` — if public
- [ ] `@PreAuthorize("hasAuthority('...')")` on controller methods — if authenticated
- [ ] `/swagger-setup` — OpenAPI annotations & verification
- [ ] Create test files (Service unit tests, Controller integration tests)
