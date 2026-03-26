package lk.helphub.api.admin.application.services;

import lk.helphub.api.admin.application.dto.AdminVerificationRequest;
import java.util.UUID;

public interface AdminVerificationService {
    void verifyProvider(UUID providerProfileId, AdminVerificationRequest request);
    void verifyCertificate(UUID certificateId);
}
