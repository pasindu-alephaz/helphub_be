package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.ProviderIdentityDocumentRequest;
import lk.helphub.api.application.dto.ProviderIdentityDocumentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProviderDocumentService {
    ProviderIdentityDocumentResponse addIdentityDocument(UUID providerId, ProviderIdentityDocumentRequest request, List<MultipartFile> images);
    List<ProviderIdentityDocumentResponse> getIdentityDocuments(UUID providerId);
    void deleteIdentityDocument(UUID providerId, UUID documentId);
}
