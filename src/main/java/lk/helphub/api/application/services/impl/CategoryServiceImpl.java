package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.dto.*;
import lk.helphub.api.application.services.CategoryService;
import lk.helphub.api.domain.entity.ServiceCategory;
import lk.helphub.api.domain.repository.ServiceCategoryRepository;
import lk.helphub.api.domain.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lk.helphub.api.domain.exception.ResourceNotFoundException;

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
                .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Override
    public CategoryResponse getSubcategoryById(UUID categoryId, UUID subCategoryId) {
        ServiceCategory subcategory = categoryRepository.findById(subCategoryId)
                .filter(c -> c.getDeletedAt() == null && c.getParent() != null)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found"));

        if (!subcategory.getParent().getId().equals(categoryId)) {
            throw new IllegalArgumentException("Subcategory does not belong to the specified category");
        }

        return mapToResponse(subcategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryUpdateRequest request) {
        ServiceCategory category = categoryRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null && c.getParent() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Top-level category not found"));

        updateBaseFields(category, request);

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse updateSubcategory(UUID categoryId, UUID subCategoryId, SubcategoryUpdateRequest request) {
        ServiceCategory subcategory = categoryRepository.findById(subCategoryId)
                .filter(c -> c.getDeletedAt() == null && c.getParent() != null)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found"));

        if (!subcategory.getParent().getId().equals(categoryId)) {
            throw new IllegalArgumentException("Subcategory does not belong to the specified category");
        }

        updateBaseFields(subcategory, request);

        if (request.getParentId() != null) {
            ServiceCategory parent = categoryRepository.findById(request.getParentId())
                    .filter(c -> c.getDeletedAt() == null)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            subcategory.setParent(parent);
        }

        return mapToResponse(categoryRepository.save(subcategory));
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        ServiceCategory category = categoryRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null && c.getParent() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Top-level category not found"));
        
        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteSubcategory(UUID categoryId, UUID subCategoryId) {
        ServiceCategory subcategory = categoryRepository.findById(subCategoryId)
                .filter(c -> c.getDeletedAt() == null && c.getParent() != null)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found"));

        if (!subcategory.getParent().getId().equals(categoryId)) {
            throw new IllegalArgumentException("Subcategory does not belong to the specified category");
        }
        
        subcategory.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(subcategory);
    }

    @Override
    @Transactional
    public CategoryResponse requestCategory(CategoryCreateRequest request) {
        ServiceCategory category = ServiceCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status("pending")
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
    public CategoryResponse requestSubcategory(SubcategoryCreateRequest request) {
        if (request.getParentId() == null) {
            throw new IllegalArgumentException("Parent category ID is required for subcategory requests");
        }

        ServiceCategory parent = categoryRepository.findById(request.getParentId())
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));

        ServiceCategory subcategory = ServiceCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status("pending")
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
    public List<CategoryResponse> getPendingRequests() {
        return categoryRepository.findAllByDeletedAtIsNull().stream()
                .filter(c -> "pending".equals(c.getStatus()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponse approveRequest(UUID id) {
        ServiceCategory category = categoryRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null && "pending".equals(c.getStatus()))
                .orElseThrow(() -> new ResourceNotFoundException("Pending category request not found"));

        category.setStatus("active");
        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void rejectRequest(UUID id) {
        ServiceCategory category = categoryRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null && "pending".equals(c.getStatus()))
                .orElseThrow(() -> new ResourceNotFoundException("Pending category request not found"));

        category.setStatus("rejected");
        category.setDeletedAt(LocalDateTime.now()); // Optional: soft delete on rejection
        categoryRepository.save(category);
    }

    private void updateBaseFields(ServiceCategory entity, BaseCategoryRequest request) {
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        entity.setDisplayOrder(request.getDisplayOrder());

        if (request.getIconId() != null) {
            imageRepository.findById(request.getIconId())
                    .ifPresent(entity::setIcon);
        } else {
            entity.setIcon(null);
        }
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
