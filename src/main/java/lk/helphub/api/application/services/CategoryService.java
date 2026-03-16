package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.*;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse createCategory(CategoryCreateRequest request);
    CategoryResponse createSubcategory(SubcategoryCreateRequest request);
    List<CategoryResponse> getAllCategories(boolean hierarchical);
    CategoryResponse getCategoryById(UUID id);
    CategoryResponse updateCategory(UUID id, BaseCategoryRequest request);
    void deleteCategory(UUID id);
}
