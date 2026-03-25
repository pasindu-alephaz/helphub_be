package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.AdminVerificationRequest;
import java.util.UUID;

public interface AdminVerificationService {
    void verifyProvider(UUID providerProfileId, AdminVerificationRequest request);
    void verifyCertificate(UUID certificateId);
}
