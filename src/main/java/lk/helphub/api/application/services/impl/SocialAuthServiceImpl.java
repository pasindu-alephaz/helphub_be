package lk.helphub.api.application.services.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lk.helphub.api.application.dto.AuthResponse;
import lk.helphub.api.application.dto.SocialAuthRequest;
import lk.helphub.api.application.dto.SocialIdentity;
import lk.helphub.api.application.services.SocialAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SocialAuthServiceImpl implements SocialAuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id:default-client-id}")
    private String googleClientId;

    private static final String APPLE_JWKS_URL = "https://appleid.apple.com/auth/keys";
    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    private final Cache<String, SocialIdentity> socialPendingCache = Caffeine.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    @Override
    public AuthResponse loginWithGoogle(SocialAuthRequest request) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String givenName = (String) payload.get("given_name");
                String familyName = (String) payload.get("family_name");
                String name = (String) payload.get("name");

                SocialIdentity identity = SocialIdentity.builder()
                        .email(payload.getEmail())
                        .googleId(payload.getSubject())
                        .fullName(name != null ? name : (givenName + " " + (familyName != null ? familyName : "")).trim())
                        .displayName(givenName != null ? givenName : name)
                        .pictureUrl((String) payload.get("picture"))
                        .build();

                // Override from request if provided
                if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
                    identity.setFullName(request.getFirstName() + (request.getLastName() != null ? " " + request.getLastName() : ""));
                    identity.setDisplayName(request.getFirstName());
                }

                String pendingToken = UUID.randomUUID().toString();
                socialPendingCache.put(pendingToken, identity);

                return AuthResponse.builder()
                        .phoneVerificationRequired(true)
                        .pendingToken(pendingToken)
                        .build();
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
    public AuthResponse loginWithApple(SocialAuthRequest request) {
        try {
            String[] splitToken = request.getToken().split("\\.");
            if (splitToken.length < 2) {
                throw new IllegalArgumentException("Invalid token format");
            }
            String headerJson = new String(java.util.Base64.getUrlDecoder().decode(splitToken[0]));
            String kid = "";
            int kidIdx = headerJson.indexOf("\"kid\":\"");
            if (kidIdx > 0) {
                int start = kidIdx + 7;
                int end = headerJson.indexOf("\"", start);
                kid = headerJson.substring(start, end);
            } else {
                throw new IllegalArgumentException("Missing kid in Apple token header");
            }

            JwkProvider provider = new JwkProviderBuilder(new URL(APPLE_JWKS_URL))
                    .cached(10, 24, TimeUnit.HOURS)
                    .build();
            com.auth0.jwk.Jwk jwk = provider.get(kid);
            RSAPublicKey publicKey = (RSAPublicKey) jwk.getPublicKey();

            io.jsonwebtoken.JwtParser jwtParser = io.jsonwebtoken.Jwts.parserBuilder()
                    .requireIssuer(APPLE_ISSUER)
                    .setSigningKey(publicKey)
                    .build();

            io.jsonwebtoken.Jws<io.jsonwebtoken.Claims> claimsJws = jwtParser.parseClaimsJws(request.getToken());
            io.jsonwebtoken.Claims claims = claimsJws.getBody();

            SocialIdentity identity = SocialIdentity.builder()
                    .appleId(claims.getSubject())
                    .email(claims.get("email", String.class))
                    .fullName(request.getFirstName() != null ? (request.getFirstName() + (request.getLastName() != null ? " " + request.getLastName() : "")) : "Apple User")
                    .displayName(request.getFirstName() != null ? request.getFirstName() : "Apple")
                    .build();

            String pendingToken = UUID.randomUUID().toString();
            socialPendingCache.put(pendingToken, identity);

            return AuthResponse.builder()
                    .phoneVerificationRequired(true)
                    .pendingToken(pendingToken)
                    .build();

        } catch (Exception e) {
            log.error("Apple token verification failed", e);
            throw new IllegalArgumentException("Apple token verification failed: " + e.getMessage());
        }
    }

    @Override
    public Optional<SocialIdentity> getPendingIdentity(String token) {
        return Optional.ofNullable(socialPendingCache.getIfPresent(token));
    }
}
