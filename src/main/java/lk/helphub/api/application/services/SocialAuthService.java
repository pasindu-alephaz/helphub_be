package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.AuthResponse;
import lk.helphub.api.application.dto.SocialAuthRequest;
import lk.helphub.api.application.dto.SocialIdentity;

import java.util.Optional;

public interface SocialAuthService {
    AuthResponse loginWithGoogle(SocialAuthRequest request);
    AuthResponse loginWithApple(SocialAuthRequest request);
    Optional<SocialIdentity> getPendingIdentity(String token);
}
