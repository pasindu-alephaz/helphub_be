package lk.helphub.api.admin.application.services;
import lk.helphub.api.application.services.MailService;
import lk.helphub.api.application.services.RefreshTokenService;
import lk.helphub.api.application.dto.AuthResponse;
import lk.helphub.api.application.dto.LoginRequest;
import lk.helphub.api.application.dto.VerifyOtpRequest;
import lk.helphub.api.domain.entity.LoginOtp;
import lk.helphub.api.domain.entity.RefreshToken;
import lk.helphub.api.domain.entity.Role;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.repository.LoginOtpRepository;
import lk.helphub.api.domain.repository.RoleRepository;
import lk.helphub.api.domain.repository.UserRepository;
import lk.helphub.api.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final LoginOtpRepository loginOtpRepository;
    private final MailService mailService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse loginAdmin(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify the user has ADMIN role OR admin-access permission
        if (!hasAdminAccess(user)) {
            throw new AccessDeniedException("Access denied. This login is for administrators only.");
        }

        // If 2FA is enabled, send OTP and return challenge instead of JWT
        if (user.isTwoFactorEnabled()) {
            // Invalidate any previous unused OTPs
            List<LoginOtp> previous = loginOtpRepository.findByUserAndUsedAtIsNull(user);
            for (LoginOtp otp : previous) {
                otp.setUsedAt(LocalDateTime.now());
                loginOtpRepository.save(otp);
            }

            String otp = String.format("%06d", new java.util.Random().nextInt(999999));
            LoginOtp loginOtp = LoginOtp.builder()
                    .user(user)
                    .otp(otp)
                    .expiresAt(LocalDateTime.now().plusMinutes(10))
                    .build();
            loginOtpRepository.save(loginOtp);

            mailService.sendMail(
                    user.getEmail(),
                    "Your HelpHub Admin Login OTP",
                    "Your one-time admin login verification code is: " + otp + "\nThis code expires in 10 minutes."
            );

            log.info("Admin 2FA OTP sent to {}", user.getEmail());
            return AuthResponse.builder().twoFactorRequired(true).build();
        }

        // 2FA not enabled — return tokens immediately
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken.getToken()).build();
    }

    @Transactional
    public AuthResponse verify2fa(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify the user has ADMIN role OR admin-access permission
        if (!hasAdminAccess(user)) {
            throw new AccessDeniedException("Access denied. This endpoint is for administrators only.");
        }

        LoginOtp loginOtp = loginOtpRepository.findByOtpAndUser(request.getOtp(), user)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

        if (loginOtp.getUsedAt() != null) {
            throw new IllegalArgumentException("OTP has already been used");
        }
        if (loginOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired");
        }

        loginOtp.setUsedAt(LocalDateTime.now());
        loginOtpRepository.save(loginOtp);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken.getToken()).build();
    }

    /**
     * Checks if the user has admin access either through the ADMIN role
     * or through the admin-access permission.
     *
     * @param user the user to check
     * @return true if the user has admin access, false otherwise
     */
    private boolean hasAdminAccess(User user) {
        // Check if user has ADMIN role
        boolean hasAdminRole = user.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()));

        if (hasAdminRole) {
            return true;
        }

        // Check if user has admin-access permission via any of their roles
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission -> "admin-access".equals(permission.getSlug()));
    }
}
