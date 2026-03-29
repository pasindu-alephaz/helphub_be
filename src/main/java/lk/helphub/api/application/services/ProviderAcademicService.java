package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.UserEducationRequest;
import lk.helphub.api.application.dto.UserEducationResponse;

import java.util.List;
import java.util.UUID;

public interface ProviderAcademicService {
    UserEducationResponse addAcademicQualification(String username, UserEducationRequest request);
    UserEducationResponse updateAcademicQualification(String username, UUID educationId, UserEducationRequest request);
    List<UserEducationResponse> getAcademicQualifications(String username);
    void deleteAcademicQualification(String username, UUID educationId);
}
