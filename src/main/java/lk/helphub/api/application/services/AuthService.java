package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.*;
import lk.helphub.api.domain.entity.PhoneOtp;
import lk.helphub.api.domain.entity.RefreshToken;
import lk.helphub.api.domain.entity.Role;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.enums.PhoneOtpPurpose;
import lk.helphub.api.domain.repository.PhoneOtpRepository;
import lk.helphub.api.domain.repository.RoleRepository;
import lk.helphub.api.domain.repository.UserRepository;
import lk.helphub.api.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PhoneOtpRepository phoneOtpRepository;
    private final SmsService smsService;
    private final SocialAuthService socialAuthService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse sendPhoneOtp(PhoneInitRequest request) {
        String phoneNumber = request.getPhoneNumber();
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        
        PhoneOtpPurpose purpose = PhoneOtpPurpose.PHONE_LOGIN;
        if (request.getPendingToken() != null && !request.getPendingToken().isBlank()) {
            // Validate that the social session actually exists before marking as SOCIAL_LINK
            if (socialAuthService.getPendingIdentity(request.getPendingToken()).isEmpty()) {
                throw new IllegalArgumentException("Social session expired or invalid");
            }
            purpose = PhoneOtpPurpose.SOCIAL_LINK;
        } else {
            boolean userExists = userRepository.findByPhoneNumber(phoneNumber).isPresent();
            purpose = userExists ? PhoneOtpPurpose.PHONE_LOGIN : PhoneOtpPurpose.PHONE_REGISTER;
        }

        // Invalidate any previous unused OTPs for this phone
        List<PhoneOtp> previous = phoneOtpRepository.findByPhoneNumberAndUsedAtIsNull(phoneNumber);
        for (PhoneOtp oldOtp : previous) {
            oldOtp.setUsedAt(LocalDateTime.now());
            phoneOtpRepository.save(oldOtp);
        }

        PhoneOtp phoneOtp = PhoneOtp.builder()
                .phoneNumber(phoneNumber)
                .otp(otp)
                .purpose(purpose)
                .pendingToken(request.getPendingToken()) // Link back to social if present
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        phoneOtpRepository.save(phoneOtp);

        log.info("[DEV] Phone OTP for {}: {}", phoneNumber, otp);
        smsService.sendSms(phoneNumber, "Your HelpHub verification code is: " + otp);

        return AuthResponse.builder()
                .phoneVerificationRequired(true)
                .build();
    }

    @Transactional
    public AuthResponse verifyPhoneOtp(PhoneOtpVerifyRequest request) {
        String phoneNumber = request.getPhoneNumber();
        String otpValue = request.getOtp();

        PhoneOtp phoneOtp = phoneOtpRepository.findByPhoneNumberAndOtpAndUsedAtIsNull(phoneNumber, otpValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired OTP"));

        if (phoneOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired");
        }

        phoneOtp.setUsedAt(LocalDateTime.now());
        
        // Prioritize the pending token from request if provided, fallback to DB
        String socialToken = (request.getPendingToken() != null && !request.getPendingToken().isBlank())
                ? request.getPendingToken()
                : phoneOtp.getPendingToken();

        // Handle Social Linking logic
        if (socialToken != null && (phoneOtp.getPurpose() == PhoneOtpPurpose.SOCIAL_LINK || 
            (request.getPendingToken() != null && !request.getPendingToken().isBlank()))) {
            
            SocialIdentity identity = socialAuthService.getPendingIdentity(socialToken)
                    .orElseThrow(() -> new IllegalArgumentException("Social session expired"));

            Optional<User> userBySocial = Optional.empty();
            if (identity.getGoogleId() != null) userBySocial = userRepository.findByGoogleId(identity.getGoogleId());
            else if (identity.getAppleId() != null) userBySocial = userRepository.findByAppleId(identity.getAppleId());

            if (userBySocial.isPresent()) {
                User user = userBySocial.get();
                if (user.getPhoneNumber() == null) {
                    user.setPhoneNumber(phoneNumber);
                    user.setPhoneVerifiedAt(LocalDateTime.now());
                    userRepository.save(user);
                }
                phoneOtpRepository.save(phoneOtp);
                return generateAuthResponse(user);
            } else {
                // Check if user exists by email to link
                if (identity.getEmail() != null) {
                    Optional<User> userByEmail = userRepository.findByEmail(identity.getEmail());
                    if (userByEmail.isPresent()) {
                        User user = userByEmail.get();
                        if (identity.getGoogleId() != null) user.setGoogleId(identity.getGoogleId());
                        if (identity.getAppleId() != null) user.setAppleId(identity.getAppleId());
                        user.setPhoneNumber(phoneNumber);
                        user.setPhoneVerifiedAt(LocalDateTime.now());
                        userRepository.save(user);
                        phoneOtpRepository.save(phoneOtp);
                        return generateAuthResponse(user);
                    }
                }
                
                // New user via social - Registration required
                phoneOtpRepository.save(phoneOtp);
                return AuthResponse.builder()
                        .registrationRequired(true)
                        .pendingToken(socialToken)
                        .build();
            }
        }
        
        // Normal Phone Login/Register
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            phoneOtpRepository.save(phoneOtp);
            return generateAuthResponse(user);
        } else {
            // Use existing pendingToken if available, otherwise generate new
            String pendingToken = (phoneOtp.getPendingToken() != null) 
                    ? phoneOtp.getPendingToken() 
                    : java.util.UUID.randomUUID().toString();
            
            phoneOtp.setPendingToken(pendingToken);
            phoneOtpRepository.save(phoneOtp);
            
            return AuthResponse.builder()
                    .registrationRequired(true)
                    .pendingToken(pendingToken)
                    .build();
        }
    }

    @Transactional
    public AuthResponse completeRegistration(CompleteRegistrationRequest request) {
        PhoneOtp phoneOtp = phoneOtpRepository.findByPendingTokenAndUsedAtIsNotNull(request.getPendingToken())
                .orElse(null);

        SocialIdentity socialIdentity = socialAuthService.getPendingIdentity(request.getPendingToken())
                .orElse(null);

        if (phoneOtp == null && socialIdentity == null) {
            throw new IllegalArgumentException("Invalid registration token");
        }

        String phoneNumber = (phoneOtp != null) ? phoneOtp.getPhoneNumber() : null;
        String email = (request.getEmail() != null && !request.getEmail().isBlank()) 
                ? request.getEmail() 
                : (socialIdentity != null ? socialIdentity.getEmail() : null);

        if (email != null && userRepository.existsByEmail(email)) {
             // If user exists by email, we should have caught this in verify step for linking, 
             // but if they change email here, we check again.
            throw new IllegalArgumentException("User with this email already exists");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("USER").build()));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(phoneNumber)
                .email(email)
                .dateOfBirth(request.getDateOfBirth())
                .passwordHash(passwordEncoder.encode(java.util.UUID.randomUUID().toString()))
                .status("active")
                .userType("customer")
                .roles(Set.of(userRole))
                .phoneVerifiedAt(LocalDateTime.now())
                .googleId(socialIdentity != null ? socialIdentity.getGoogleId() : null)
                .appleId(socialIdentity != null ? socialIdentity.getAppleId() : null)
                .profileImageUrl(socialIdentity != null ? socialIdentity.getPictureUrl() : null)
                .build();
        
        if (socialIdentity != null) {
            user.setEmailVerifiedAt(LocalDateTime.now());
        }

        user = userRepository.save(user);
        return generateAuthResponse(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        // We use email as the identifier for UserDetails if present, otherwise phone number
        String identifier = (user.getEmail() != null && !user.getEmail().isBlank())
                ? user.getEmail()
                : user.getPhoneNumber();

        UserDetails userDetails = userDetailsService.loadUserByUsername(identifier);
        String accessToken = jwtUtil.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
