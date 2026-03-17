package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.AuthResponse;
import lk.helphub.api.application.dto.SocialAuthRequest;

public interface SocialAuthService {
    AuthResponse loginWithGoogle(SocialAuthRequest request);
    AuthResponse loginWithApple(SocialAuthRequest request);
}
