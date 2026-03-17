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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Service Categories", description = "APIs for users to view and request service categories")
public class CategoryController {

    private final CategoryService categoryService;

    // --- User Facing Endpoints ---

    @GetMapping("/categories")
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

    @GetMapping("/categories/{id}")
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

    @GetMapping("/categories/{categoryId}/subcategories/{subCategoryId}")
    @Operation(summary = "Get subcategory details", description = "Retrieves details of a specific subcategory by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subcategory retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Subcategory not found")
    })
    public ResponseEntity<ApiResponse<CategoryResponse>> getSubcategoryById(
            @PathVariable UUID categoryId,
            @PathVariable UUID subCategoryId
    ) {
        CategoryResponse subcategory = categoryService.getSubcategoryById(categoryId, subCategoryId);
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Subcategory retrieved successfully")
                .data(subcategory)
                .build());
    }

    @PostMapping("/categories/request")
    @Operation(summary = "Request new category", description = "Allows users to suggest a new top-level service category")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request submitted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<ApiResponse<CategoryResponse>> requestCategory(@Valid @RequestBody CategoryCreateRequest request) {
        CategoryResponse response = categoryService.requestCategory(request);
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Category request submitted successfully. Admin will review it.")
                .data(response)
                .build());
    }

    @PostMapping("/categories/{categoryId}/subcategories/request")
    @Operation(summary = "Request new subcategory", description = "Allows users to suggest a new subcategory for an existing category")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request submitted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<ApiResponse<CategoryResponse>> requestSubcategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody SubcategoryCreateRequest request
    ) {
        request.setParentId(categoryId);
        CategoryResponse response = categoryService.requestSubcategory(request);
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Subcategory request submitted successfully. Admin will review it.")
                .data(response)
                .build());
    }
}
