package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.dto.SendVerificationRequest;
import lk.helphub.api.application.dto.SendVerificationResponse;
import lk.helphub.api.application.dto.VerifyOtpRequest;
import lk.helphub.api.application.services.MailService;
import lk.helphub.api.application.services.SmsService;
import lk.helphub.api.application.services.VerificationService;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.entity.VerificationOtp;
import lk.helphub.api.domain.repository.UserRepository;
import lk.helphub.api.domain.repository.VerificationOtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationServiceImpl implements VerificationService {

    private final VerificationOtpRepository verificationOtpRepository;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final SmsService smsService;

    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    @Transactional
    public SendVerificationResponse sendVerificationOtp(SendVerificationRequest request) {
        if (request.getEmail() == null && request.getPhoneNumber() == null) {
            throw new IllegalArgumentException("At least one of email or phone number must be provided");
        }

        SendVerificationResponse.SendVerificationResponseBuilder responseBuilder = SendVerificationResponse.builder();

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String token = generateAndSaveOtp(request.getEmail(), "email");
            sendEmailOtp(request.getEmail(), token);
            responseBuilder.emailToken(token);
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            String token = generateAndSaveOtp(request.getPhoneNumber(), "phone");
            sendPhoneOtp(request.getPhoneNumber(), token);
            responseBuilder.phoneToken(token);
        }

        return responseBuilder.build();
    }

    @Override
    @Transactional
    public void verifyOtp(VerifyOtpRequest request) {
        VerificationOtp otpRecord = verificationOtpRepository
                .findByTokenAndOtp(request.getToken(), request.getOtp())
                .orElseThrow(() -> new IllegalArgumentException("Invalid token or OTP"));

        if (otpRecord.getUsedAt() != null) {
            throw new IllegalArgumentException("OTP has already been used");
        }

        if (otpRecord.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired");
        }

        // Mark OTP as used
        otpRecord.setUsedAt(LocalDateTime.now());
        verificationOtpRepository.save(otpRecord);

        // Update user's verified timestamp
        String type = otpRecord.getType();
        String target = otpRecord.getTarget();

        if ("email".equals(type)) {
            User user = userRepository.findByEmail(target)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + target));
            user.setEmailVerifiedAt(LocalDateTime.now());
            userRepository.save(user);
        } else if ("phone".equals(type)) {
            User user = userRepository.findByPhoneNumber(target)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with phone number: " + target));
            user.setPhoneVerifiedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    private String generateAndSaveOtp(String target, String type) {
        // Invalidate previous unused OTPs for this target + type
        List<VerificationOtp> activeOtps = verificationOtpRepository
                .findByTargetAndTypeAndUsedAtIsNull(target, type);
        for (VerificationOtp otp : activeOtps) {
            otp.setUsedAt(LocalDateTime.now());
            verificationOtpRepository.save(otp);
        }

        // Generate new OTP and token
        String otp = String.format("%06d", SECURE_RANDOM.nextInt(999999));
        String token = UUID.randomUUID().toString().replace("-", "");

        VerificationOtp verificationOtp = VerificationOtp.builder()
                .token(token)
                .otp(otp)
                .type(type)
                .target(target)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .build();

        verificationOtpRepository.save(verificationOtp);
        log.info("Generated verification OTP for {} ({}): {}", target, type, otp);

        return token;
    }

    private void sendEmailOtp(String email, String token) {
        // Retrieve the OTP from the saved record
        VerificationOtp otpRecord = verificationOtpRepository
                .findByTargetAndTypeAndUsedAtIsNull(email, "email")
                .stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("OTP record not found"));

        String subject = "Email Verification";
        String body = "Your email verification OTP is: " + otpRecord.getOtp()
                + "\nThis OTP will expire in " + OTP_EXPIRY_MINUTES + " minutes.";
        mailService.sendMail(email, subject, body);
    }

    private void sendPhoneOtp(String phoneNumber, String token) {
        VerificationOtp otpRecord = verificationOtpRepository
                .findByTargetAndTypeAndUsedAtIsNull(phoneNumber, "phone")
                .stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("OTP record not found"));

        String message = "Your HelpHub verification OTP is: " + otpRecord.getOtp()
                + ". Valid for " + OTP_EXPIRY_MINUTES + " minutes.";
        smsService.sendSms(phoneNumber, message);
    }
}
