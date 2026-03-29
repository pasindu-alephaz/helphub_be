package lk.helphub.api.application.services.impl;

import jakarta.transaction.Transactional;
import lk.helphub.api.application.dto.UserEducationRequest;
import lk.helphub.api.application.dto.UserEducationResponse;
import lk.helphub.api.application.services.ProviderAcademicService;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.entity.UserEducation;
import lk.helphub.api.domain.repository.UserEducationRepository;
import lk.helphub.api.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderAcademicServiceImpl implements ProviderAcademicService {

    private final UserEducationRepository userEducationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserEducationResponse addAcademicQualification(String username, UserEducationRequest request) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserEducation education = new UserEducation();
        education.setUser(user);
        education.setCertificateName(request.getCertificateName());
        education.setUniversity(request.getUniversity());
        education.setEducationalLevel("CERTIFICATE"); // Default or map if added to request

        education = userEducationRepository.save(education);
        return mapToResponse(education);
    }

    @Override
    @Transactional
    public UserEducationResponse updateAcademicQualification(String username, UUID educationId, UserEducationRequest request) {
        UserEducation education = userEducationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("Qualification not found"));
        
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!education.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Qualification does not belong to user");
        }

        education.setCertificateName(request.getCertificateName());
        education.setUniversity(request.getUniversity());
        education = userEducationRepository.save(education);

        return mapToResponse(education);
    }

    @Override
    public List<UserEducationResponse> getAcademicQualifications(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userEducationRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAcademicQualification(String username, UUID educationId) {
        UserEducation education = userEducationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("Qualification not found"));
                
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!education.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Qualification does not belong to user");
        }
        userEducationRepository.delete(education);
    }

    private UserEducationResponse mapToResponse(UserEducation edu) {
        return UserEducationResponse.builder()
                .id(edu.getId())
                .certificateName(edu.getCertificateName())
                .university(edu.getUniversity())
                .createdAt(edu.getCreatedAt())
                .updatedAt(edu.getUpdatedAt())
                .build();
    }
}
