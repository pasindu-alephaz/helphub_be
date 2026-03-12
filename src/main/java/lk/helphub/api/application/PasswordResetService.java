package lk.helphub.api.application;

import lk.helphub.api.application.dto.ForgotPasswordRequest;
import lk.helphub.api.application.dto.ResetPasswordRequest;

public interface PasswordResetService {
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
