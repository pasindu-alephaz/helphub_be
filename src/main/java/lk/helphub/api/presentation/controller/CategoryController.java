package lk.helphub.api.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.helphub.api.application.dto.*;
import lk.helphub.api.application.services.CategoryService;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Service Categories", description = "APIs for managing service categories and subcategories")
public class CategoryController {

    private final CategoryService categoryService;

    // --- Global/Shared Endpoints ---

    @GetMapping("/api/v1/categories")
    @Operation(summary = "List categories", description = "Retrieves all service categories in a unified hierarchical or flat structure")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(
            @RequestParam(defaultValue = "true") boolean hierarchical
    ) {
        List<CategoryResponse> categories = categoryService.getAllCategories(hierarchical);
        return ResponseEntity.ok(ApiResponse.<List<CategoryResponse>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Categories retrieved successfully")
                .data(categories)
                .build());
    }

    // --- Category Specific Endpoints ---

    @PostMapping("/api/v1/categories")
    @Operation(summary = "Create top-level category", description = "Creates a new top-level service category")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Category created successfully")
                .data(response)
                .build());
    }

    @GetMapping("/api/v1/categories/{id}")
    @Operation(summary = "Get category details", description = "Retrieves details of a specific top-level category by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable UUID id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Category retrieved successfully")
                .data(category)
                .build());
    }

    @PutMapping("/api/v1/categories/{id}")
    @Operation(summary = "Update category", description = "Updates an existing top-level service category")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryUpdateRequest request
    ) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Category updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/api/v1/categories/{id}")
    @Operation(summary = "Delete category", description = "Deletes a top-level service category (soft delete)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Category deleted successfully")
                .build());
    }

    // --- Subcategory Specific Endpoints ---

    @PostMapping("/api/v1/subcategories")
    @Operation(summary = "Create subcategory", description = "Creates a new subcategory using parentId in request body")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subcategory created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Parent category not found")
    })
    public ResponseEntity<ApiResponse<CategoryResponse>> createSubcategory(@Valid @RequestBody SubcategoryCreateRequest request) {
        CategoryResponse response = categoryService.createSubcategory(request);
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Subcategory created successfully")
                .data(response)
                .build());
    }

    @GetMapping("/api/v1/subcategories/{id}")
    @Operation(summary = "Get subcategory details", description = "Retrieves details of a specific subcategory by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subcategory retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subcategory not found")
    })
    public ResponseEntity<ApiResponse<CategoryResponse>> getSubcategoryById(@PathVariable UUID id) {
        CategoryResponse subcategory = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Subcategory retrieved successfully")
                .data(subcategory)
                .build());
    }

    @PutMapping("/api/v1/subcategories/{id}")
    @Operation(summary = "Update subcategory", description = "Updates an existing subcategory")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subcategory updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subcategory not found")
    })
    public ResponseEntity<ApiResponse<CategoryResponse>> updateSubcategory(
            @PathVariable UUID id,
            @Valid @RequestBody SubcategoryUpdateRequest request
    ) {
        CategoryResponse response = categoryService.updateSubcategory(id, request);
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Subcategory updated successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/api/v1/subcategories/{id}")
    @Operation(summary = "Delete subcategory", description = "Deletes a subcategory (soft delete)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subcategory deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subcategory not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteSubcategory(@PathVariable UUID id) {
        categoryService.deleteSubcategory(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Subcategory deleted successfully")
                .build());
    }
}
