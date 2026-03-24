package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.ForgotPasswordRequest;
import lk.helphub.api.application.dto.ResetPasswordRequest;

public interface PasswordResetService {
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
