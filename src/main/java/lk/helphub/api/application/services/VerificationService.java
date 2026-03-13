package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.SendVerificationRequest;
import lk.helphub.api.application.dto.SendVerificationResponse;
import lk.helphub.api.application.dto.VerifyOtpRequest;

public interface VerificationService {
    SendVerificationResponse sendVerificationOtp(SendVerificationRequest request);
    void verifyOtp(VerifyOtpRequest request);
}
