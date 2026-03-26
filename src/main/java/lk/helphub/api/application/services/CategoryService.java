package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.*;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse createCategory(CategoryCreateRequest request, MultipartFile image);
    CategoryResponse createSubcategory(SubcategoryCreateRequest request, MultipartFile image);
    List<CategoryResponse> getAllCategories(boolean hierarchical);
    List<CategoryResponse> getAllSubcategories();
    CategoryResponse getCategoryById(UUID id);
    CategoryResponse getSubcategoryById(UUID categoryId, UUID subCategoryId);
    CategoryResponse updateCategory(UUID id, CategoryUpdateRequest request, MultipartFile image);
    CategoryResponse updateSubcategory(UUID categoryId, UUID subCategoryId, SubcategoryUpdateRequest request, MultipartFile image);
    void deleteCategory(UUID id);
    void deleteSubcategory(UUID categoryId, UUID subCategoryId);

    // Request & Approval Flow
    CategoryResponse requestCategory(CategoryCreateRequest request, MultipartFile image);
    CategoryResponse requestSubcategory(SubcategoryCreateRequest request, MultipartFile image);
    List<CategoryResponse> getPendingRequests();
    CategoryResponse approveRequest(UUID id);
    void rejectRequest(UUID id);
}
