package lk.helphub.api.application.services.impl;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lk.helphub.api.application.dto.AuthResponse;
import lk.helphub.api.application.dto.SocialAuthRequest;
import lk.helphub.api.application.services.SocialAuthService;
import lk.helphub.api.domain.entity.Role;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.repository.RoleRepository;
import lk.helphub.api.domain.repository.UserRepository;
import lk.helphub.api.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialAuthServiceImpl implements SocialAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.security.oauth2.client.registration.google.client-id:default-client-id}")
    private String googleClientId;

    private static final String APPLE_JWKS_URL = "https://appleid.apple.com/auth/keys";
    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    @Override
    @Transactional
    public AuthResponse loginWithGoogle(SocialAuthRequest request) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String googleId = payload.getSubject();
                String name = (String) payload.get("name");
                String givenName = (String) payload.get("given_name");
                String familyName = (String) payload.get("family_name");
                String pictureUrl = (String) payload.get("picture");

                // Fallback for names if not explicitly provided
                if (givenName == null) givenName = name;
                if (familyName == null) familyName = "";

                // Prefer given name from request if not in payload
                if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
                    givenName = request.getFirstName();
                }
                if (request.getLastName() != null && !request.getLastName().isBlank()) {
                    familyName = request.getLastName();
                }

                User user = findOrCreateGoogleUser(email, googleId, givenName, familyName, pictureUrl);
                return generateAuthResponse(user);
            } else {
                log.error("Invalid Google ID token.");
                throw new IllegalArgumentException("Invalid Google ID token.");
            }
        } catch (Exception e) {
            log.error("Google token verification failed", e);
            throw new IllegalArgumentException("Google token verification failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public AuthResponse loginWithApple(SocialAuthRequest request) {
        try {
            // Read unverified token to get the key ID (kid) from the header
            String[] splitToken = request.getToken().split("\\.");
            if (splitToken.length < 2) {
                throw new IllegalArgumentException("Invalid token format");
            }
            // A simple decode of the header to get the kid (in production better to use a robust JSON parser)
            String headerJson = new String(java.util.Base64.getUrlDecoder().decode(splitToken[0]));
            String kid = ""; // extract kid from headerJson;
            // Simplified extraction:
            int kidIdx = headerJson.indexOf("\"kid\":\"");
            if (kidIdx > 0) {
                int start = kidIdx + 7;
                int end = headerJson.indexOf("\"", start);
                kid = headerJson.substring(start, end);
            } else {
                throw new IllegalArgumentException("Missing kid in Apple token header");
            }

            // Fetch Apple's public keys
            JwkProvider provider = new JwkProviderBuilder(new URL(APPLE_JWKS_URL))
                    .cached(10, 24, TimeUnit.HOURS)
                    .build();
            Jwk jwk = provider.get(kid);
            RSAPublicKey publicKey = (RSAPublicKey) jwk.getPublicKey();

            // Verify the token
            JwtParser jwtParser = Jwts.parserBuilder()
                    .requireIssuer(APPLE_ISSUER)
                    .setSigningKey(publicKey)
                    .build();

            Jws<Claims> claimsJws = jwtParser.parseClaimsJws(request.getToken());
            Claims claims = claimsJws.getBody();

            String appleId = claims.getSubject();
            String email = claims.get("email", String.class);

            // Apple only provides name on the first login via the frontend client,
            // so we rely on the request body if they are explicitly passed.
            String firstName = request.getFirstName() != null ? request.getFirstName() : "Apple";
            String lastName = request.getLastName() != null ? request.getLastName() : "User";

            User user = findOrCreateAppleUser(email, appleId, firstName, lastName);
            return generateAuthResponse(user);

        } catch (Exception e) {
            log.error("Apple token verification failed", e);
            throw new IllegalArgumentException("Apple token verification failed: " + e.getMessage());
        }
    }

    private User findOrCreateGoogleUser(String email, String googleId, String firstName, String lastName, String pictureUrl) {
        // Try finding by Google ID first
        Optional<User> existingUserOpt = userRepository.findByGoogleId(googleId);

        if (existingUserOpt.isPresent()) {
            User user = existingUserOpt.get();
            // Optional: update profile picture or other info if it changed
            if (pictureUrl != null && !pictureUrl.equals(user.getProfileImageUrl())) {
                user.setProfileImageUrl(pictureUrl);
                userRepository.save(user);
            }
            return user;
        }

        // Try finding by Email (to link accounts)
        existingUserOpt = userRepository.findByEmail(email);

        if (existingUserOpt.isPresent()) {
            User user = existingUserOpt.get();
            user.setGoogleId(googleId);
            if (user.getProfileImageUrl() == null) {
                user.setProfileImageUrl(pictureUrl);
            }
            return userRepository.save(user);
        }

        // If not found, create new user
        return createSocialUser(email, googleId, null, firstName, lastName, pictureUrl);
    }

    private User findOrCreateAppleUser(String email, String appleId, String firstName, String lastName) {
        // Try finding by Apple ID first
        Optional<User> existingUserOpt = userRepository.findByAppleId(appleId);

        if (existingUserOpt.isPresent()) {
            return existingUserOpt.get();
        }

        // Try finding by Email (to link accounts)
        // Apple allows hiding email (creates a proxy email), but it still acts as unique ID
        if (email != null && !email.isBlank()) {
            existingUserOpt = userRepository.findByEmail(email);

            if (existingUserOpt.isPresent()) {
                User user = existingUserOpt.get();
                user.setAppleId(appleId);
                return userRepository.save(user); // Link account
            }
        }

        String fallbackEmail = email != null ? email : appleId + "@privaterelay.appleid.com";
        // If not found, create new user
        return createSocialUser(fallbackEmail, null, appleId, firstName, lastName, null);
    }

    private User createSocialUser(String email, String googleId, String appleId, String firstName, String lastName, String pictureUrl) {
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("USER").build()));

        User newUser = User.builder()
                .email(email)
                .googleId(googleId)
                .appleId(appleId)
                .firstName(firstName)
                .lastName(lastName)
                .profileImageUrl(pictureUrl)
                // Generate a highly secure random password as social users don't type it
                .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString() + UUID.randomUUID().toString()))
                .status("active")
                .userType("customer")
                .roles(Set.of(userRole))
                // For social logins, we implicitly consider them verified
                .verifiedAt(java.time.LocalDateTime.now())
                .emailVerifiedAt(java.time.LocalDateTime.now())
                .build();

        return userRepository.save(newUser);
    }

    private AuthResponse generateAuthResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        var jwtToken = jwtUtil.generateToken(userDetails);
        return AuthResponse.builder().token(jwtToken).build();
    }
}
