package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.ProviderProfileResponse;
import lk.helphub.api.application.dto.ProviderRegistrationRequest;

import java.util.UUID;

public interface ProviderService {
    ProviderProfileResponse registerProvider(String email, ProviderRegistrationRequest request);
    ProviderProfileResponse getProviderProfile(String email);
}
