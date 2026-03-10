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
    ExampleResponseDto create(ExampleRequestDto dto);
    ExampleResponseDto getById(Long id);
    List<ExampleResponseDto> getAll();
    ExampleResponseDto update(Long id, ExampleRequestDto dto);
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
    public ExampleResponseDto create(ExampleRequestDto dto) {
        Example entity = new Example();
        entity.setName(dto.getName());
        return mapToDto(exampleRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ExampleResponseDto getById(Long id) {
        return mapToDto(exampleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Not found: " + id)));
    }

    // getAll(), update(), delete() follow the same pattern

    private ExampleResponseDto mapToDto(Example e) { /* map fields */ }
}
```

---

## 5. Presentation Layer — DTOs

**Path:** `presentation/dtos/{entity}/`

- Separate **request** and **response** DTOs in a sub-package
- Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Jakarta Validation on **request** DTO only
- **Never** expose JPA entities directly

```java
// Request DTO
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ExampleRequestDto {
    @NotBlank(message = "Name is required")
    @Size(max = 255)
    private String name;
}

// Response DTO
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ExampleResponseDto {
    private Long id;
    private String name;
}
```

---

## 6. Presentation Layer — Controller

**Path:** `presentation/controllers/{EntityName}Controller.java`

- `@RestController` + `@RequestMapping("/api/v1/{plural-entity}")`
- Constructor injection via `@RequiredArgsConstructor`
- `@Valid` on request body parameters
- Return `ResponseEntity<>` with appropriate status codes

> After creating the controller, follow `/swagger-setup` to add OpenAPI annotations.

```java
@RestController
@RequestMapping("/api/v1/examples")
@RequiredArgsConstructor
public class ExampleController {
    private final ExampleService exampleService;

    @PostMapping
    public ResponseEntity<ExampleResponseDto> create(@Valid @RequestBody ExampleRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exampleService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExampleResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(exampleService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ExampleResponseDto>> getAll() {
        return ResponseEntity.ok(exampleService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExampleResponseDto> update(@PathVariable Long id, @Valid @RequestBody ExampleRequestDto dto) {
        return ResponseEntity.ok(exampleService.update(id, dto));
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

If the endpoint is public, add its path to `requestMatchers(...).permitAll()`. Authenticated endpoints need no changes.

---

## 9. Set Up Swagger Documentation

Follow the `/swagger-setup` workflow to add OpenAPI annotations to your controller and DTOs.

---

## Quick Checklist

- [ ] `domain/models/{Entity}.java`
- [ ] `domain/repositories/{Entity}Repository.java`
- [ ] `application/exceptions/` — custom exceptions
- [ ] `application/services/{Entity}Service.java` + `ServiceImpl`
- [ ] `presentation/dtos/{entity}/` — request & response DTOs
- [ ] `presentation/controllers/{Entity}Controller.java`
- [ ] `application/exceptions/GlobalExceptionHandler.java` — if missing
- [ ] `infrastructure/security/SecurityConfig.java` — if public
- [ ] `/swagger-setup` — OpenAPI annotations & verification
