---
description: How to set up Swagger/OpenAPI documentation for a new API endpoint
---

# Swagger / OpenAPI Setup Rules

These rules define how to document a new API endpoint using Swagger (springdoc-openapi). Apply these **after** creating your controller, DTOs, and service.

> The project uses `springdoc-openapi-starter-webmvc-ui` (v2.6.0). Swagger UI is available at `/swagger-ui/index.html`.

---

## 1. Controller Annotations

Add these annotations to every controller and its methods:

| Annotation | Where | Purpose |
|---|---|---|
| `@Tag(name = "...", description = "...")` | Class | Groups endpoints in Swagger UI |
| `@Operation(summary = "...", description = "...")` | Method | Documents the endpoint |
| `@ApiResponse(responseCode = "...", description = "...")` | Method | Documents each possible HTTP response |
| `@Parameter(description = "...")` | Method param | Documents path/query parameters |

### Example

```java
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/examples")
@RequiredArgsConstructor
@Tag(name = "Examples", description = "Example resource management APIs")
public class ExampleController {

    @PostMapping
    @Operation(summary = "Create a new example", description = "Creates a new example resource")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Example created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = lk.helphub.api.presentation.dto.ApiResponse.class),
                examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"VALIDATION_ERROR\",\n  \"message\": \"Validation failed\",\n  \"errors\": {\n    \"name\": [\"Name is required\"]\n  }\n}")))
    })
    public ResponseEntity<ExampleResponseDto> create(@Valid @RequestBody ExampleRequestDto dto) {
        // ...
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get example by ID", description = "Retrieves a single example by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Example found"),
        @ApiResponse(responseCode = "404", description = "Example not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = lk.helphub.api.presentation.dto.ApiResponse.class),
                examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"NOT_FOUND\",\n  \"message\": \"Example not found\"\n}")))
    })
    public ResponseEntity<ExampleResponseDto> getById(
            @Parameter(description = "ID of the example to retrieve")
            @PathVariable Long id) {
        // ...
    }

    @GetMapping
    @Operation(summary = "Get all examples", description = "Retrieves a list of all examples")
    @ApiResponse(responseCode = "200", description = "List of examples retrieved")
    public ResponseEntity<List<ExampleResponseDto>> getAll() {
        // ...
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an example", description = "Updates an existing example by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Example updated successfully"),
        @ApiResponse(responseCode = "404", description = "Example not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = lk.helphub.api.presentation.dto.ApiResponse.class),
                examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"NOT_FOUND\",\n  \"message\": \"Example not found\"\n}"))),
        @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = lk.helphub.api.presentation.dto.ApiResponse.class),
                examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"VALIDATION_ERROR\",\n  \"message\": \"Validation failed\"\n}")))
    })
    public ResponseEntity<ExampleResponseDto> update(
            @Parameter(description = "ID of the example to update")
            @PathVariable Long id,
            @Valid @RequestBody ExampleRequestDto dto) {
        // ...
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an example", description = "Deletes an example by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Example deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Example not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = lk.helphub.api.presentation.dto.ApiResponse.class),
                examples = @ExampleObject(value = "{\n  \"status\": false,\n  \"status_code\": \"NOT_FOUND\",\n  \"message\": \"Example not found\"\n}")))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the example to delete")
            @PathVariable Long id) {
        // ...
    }
}
```

---

## 2. DTO Schema Annotations

Add `@Schema` annotations on **every** DTO field so they appear clearly in the Swagger UI with descriptions and example values.

| Property | Purpose |
|---|---|
| `description` | Explains the field |
| `example` | Shows a sample value in Swagger UI |
| `requiredMode` | Marks the field as required/optional |

### Example (Request DTO)

```java
import io.swagger.v3.oas.annotations.media.Schema;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ExampleRequestDto {

    @Schema(description = "Name of the example", example = "My Example", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name is required")
    @Size(max = 255)
    private String name;

    @Schema(description = "Optional description", example = "A detailed description")
    @Size(max = 1000)
    private String description;
}
```

### Example (Response DTO)

```java
import io.swagger.v3.oas.annotations.media.Schema;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ExampleResponseDto {

    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    @Schema(description = "Name of the example", example = "My Example")
    private String name;

    @Schema(description = "Description of the example", example = "A detailed description")
    private String description;
}
```

---

## 3. Avoiding Generic Payloads (Map & JSON fields)

If your payload contains a `Map<String, String>` (e.g., for translations) or other dynamic structures, Swagger UI will generate generic keys like `"additionalProp1": "string"`.

To prevent this from cluttering your Create/Update APIs (or any API accepting a payload), you **must** provide a concrete JSON string in the `@Schema(example = "...")` attribute for that field.

### Example (Map Field)

```java
    @Schema(
        description = "Name translations by language code",
        example = "{\"en\": \"English Name\", \"si\": \"Sinhala Name\", \"ta\": \"Tamil Name\"}",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Map<String, String> name;
```

---

## 4. Verify via Swagger UI

After adding all annotations:

1. Run the application: `./mvnw spring-boot:run`
2. Open Swagger UI: `http://localhost:8080/swagger-ui/index.html`
3. Confirm:
   - The new **tag** and endpoints appear in the sidebar.
   - **Request/response schemas** display descriptions and examples.
   - Use the **"Try it out"** button to test each endpoint.

---

## Quick Checklist

- [ ] Controller has `@Tag` on the class
- [ ] Every method has `@Operation` with `summary` and `description`
- [ ] Every method has `@ApiResponses` covering all possible status codes
- [ ] Path/query parameters have `@Parameter(description = "...")`
- [ ] All request DTO fields have `@Schema` with `description`, `example`, and `requiredMode`
- [ ] All response DTO fields have `@Schema` with `description` and `example`
- [ ] Map and dynamic fields have explicit JSON `@Schema(example = "...")` to prevent generic `additionalProp1` payloads
- [ ] Verified endpoints appear correctly in Swagger UI
