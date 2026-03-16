package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.dto.*;
import lk.helphub.api.application.services.CategoryService;
import lk.helphub.api.domain.entity.ServiceCategory;
import lk.helphub.api.domain.repository.ServiceCategoryRepository;
import lk.helphub.api.domain.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final ServiceCategoryRepository categoryRepository;
    private final ImageRepository imageRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        ServiceCategory category = ServiceCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(request.getStatus())
                .displayOrder(request.getDisplayOrder())
                .build();

        if (request.getIconId() != null) {
            imageRepository.findById(request.getIconId())
                    .ifPresent(category::setIcon);
        }

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse createSubcategory(SubcategoryCreateRequest request) {
        if (request.getParentId() == null) {
            throw new IllegalArgumentException("Parent category ID is required for subcategory creation");
        }

        ServiceCategory parent = categoryRepository.findById(request.getParentId())
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));

        ServiceCategory subcategory = ServiceCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(request.getStatus())
                .displayOrder(request.getDisplayOrder())
                .parent(parent)
                .build();

        if (request.getIconId() != null) {
            imageRepository.findById(request.getIconId())
                    .ifPresent(subcategory::setIcon);
        }

        return mapToResponse(categoryRepository.save(subcategory));
    }

    @Override
    public List<CategoryResponse> getAllCategories(boolean hierarchical) {
        if (hierarchical) {
            return categoryRepository.findAllByParentIsNullAndDeletedAtIsNull().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        return categoryRepository.findAllByDeletedAtIsNull().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, BaseCategoryRequest request) {
        ServiceCategory category = categoryRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setStatus(request.getStatus());
        category.setDisplayOrder(request.getDisplayOrder());

        if (request instanceof SubcategoryCreateRequest subRequest && subRequest.getParentId() != null) {
            ServiceCategory parent = categoryRepository.findById(subRequest.getParentId())
                    .filter(c -> c.getDeletedAt() == null)
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            category.setParent(parent);
        }

        if (request.getIconId() != null) {
            imageRepository.findById(request.getIconId())
                    .ifPresent(category::setIcon);
        }

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        ServiceCategory category = categoryRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.setDeletedAt(java.time.LocalDateTime.now());
        categoryRepository.save(category);
    }

    private CategoryResponse mapToResponse(ServiceCategory entity) {
        CategoryResponse response = CategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .displayOrder(entity.getDisplayOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

        if (entity.getParent() != null) {
            response.setParentId(entity.getParent().getId());
        }

        if (entity.getIcon() != null) {
            response.setIconId(entity.getIcon().getId());
        }

        if (entity.getSubcategories() != null && !entity.getSubcategories().isEmpty()) {
            response.setSubcategories(entity.getSubcategories().stream()
                    .filter(s -> s.getDeletedAt() == null)
                    .map(this::mapToResponse)
                    .collect(Collectors.toList()));
        }

        return response;
    }
}
