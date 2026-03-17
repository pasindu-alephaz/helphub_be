package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.*;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse createCategory(CategoryCreateRequest request);
    CategoryResponse createSubcategory(SubcategoryCreateRequest request);
    List<CategoryResponse> getAllCategories(boolean hierarchical);
    CategoryResponse getCategoryById(UUID id);
    CategoryResponse updateCategory(UUID id, CategoryUpdateRequest request);
    CategoryResponse updateSubcategory(UUID id, SubcategoryUpdateRequest request);
    void deleteCategory(UUID id);
    void deleteSubcategory(UUID id);

    // Request & Approval Flow
    CategoryResponse requestCategory(CategoryCreateRequest request);
    CategoryResponse requestSubcategory(SubcategoryCreateRequest request);
    List<CategoryResponse> getPendingRequests();
    CategoryResponse approveRequest(UUID id);
    void rejectRequest(UUID id);
}
