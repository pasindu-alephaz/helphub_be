package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.services.PasswordResetService;
import lk.helphub.api.application.dto.ForgotPasswordRequest;
import lk.helphub.api.application.dto.ResetPasswordRequest;
import lk.helphub.api.domain.entity.PasswordReset;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.repository.PasswordResetRepository;
import lk.helphub.api.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final PasswordEncoder passwordEncoder;
    private final lk.helphub.api.application.services.MailService mailService;


    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + request.getEmail()));

        // Invalidate previous tokens
        List<PasswordReset> activeResets = passwordResetRepository.findByUserAndUsedAtIsNull(user);
        for (PasswordReset pr : activeResets) {
            pr.setUsedAt(LocalDateTime.now());
            passwordResetRepository.save(pr);
        }

        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        PasswordReset passwordReset = PasswordReset.builder()
                .user(user)
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        passwordResetRepository.save(passwordReset);

        String subject = "Password Reset Request";
        String body = "Your Password Reset OTP is: " + otp + "\nThis OTP will expire in 1 hour.";
        mailService.sendMail(user.getEmail(), subject, body);

        log.info("Password reset OTP for {}: {}", user.getEmail(), otp);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordReset passwordReset = passwordResetRepository.findByOtp(request.getOtp())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired OTP"));

        if (passwordReset.getUsedAt() != null || passwordReset.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        User user = passwordReset.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordReset.setUsedAt(LocalDateTime.now());
        passwordResetRepository.save(passwordReset);
    }
}
