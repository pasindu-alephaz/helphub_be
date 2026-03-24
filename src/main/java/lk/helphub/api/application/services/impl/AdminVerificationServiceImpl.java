package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.dto.AdminVerificationRequest;
import lk.helphub.api.application.services.AdminVerificationService;
import lk.helphub.api.domain.entity.ProviderCertificate;
import lk.helphub.api.domain.entity.ProviderProfile;
import lk.helphub.api.domain.enums.VerificationStatus;
import lk.helphub.api.domain.repository.ProviderCertificateRepository;
import lk.helphub.api.domain.repository.ProviderProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminVerificationServiceImpl implements AdminVerificationService {

    private final ProviderProfileRepository providerProfileRepository;
    private final ProviderCertificateRepository certificateRepository;

    @Override
    @Transactional
    public void verifyProvider(UUID providerProfileId, AdminVerificationRequest request) {
        ProviderProfile profile = providerProfileRepository.findById(providerProfileId)
                .orElseThrow(() -> new IllegalArgumentException("Provider profile not found"));
        
        profile.setVerificationStatus(request.getStatus());
        profile.setNotes(request.getNotes());
        
        if (request.getStatus() == VerificationStatus.APPROVED) {
            profile.setVerifiedAt(LocalDateTime.now());
            profile.setVerifiedBadge(true); // Auto-assign badge on approval as per requirement 6
        } else {
            profile.setVerifiedBadge(false);
        }
        
        providerProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public void verifyCertificate(UUID certificateId) {
        ProviderCertificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new IllegalArgumentException("Certificate not found"));
        
        certificate.setVerifiedAt(LocalDateTime.now());
        certificateRepository.save(certificate);
    }
}
