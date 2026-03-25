package lk.helphub.api.domain.entity;

import jakarta.persistence.*;
import lk.helphub.api.domain.enums.PhoneOtpPurpose;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "phone_otps")
public class PhoneOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 6)
    private String otp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PhoneOtpPurpose purpose;

    @Column(name = "pending_token")
    private String pendingToken;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public PhoneOtp() {}

    public PhoneOtp(UUID id, String phoneNumber, String otp, PhoneOtpPurpose purpose, String pendingToken, LocalDateTime expiresAt, LocalDateTime usedAt, LocalDateTime createdAt) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.purpose = purpose;
        this.pendingToken = pendingToken;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public PhoneOtpPurpose getPurpose() { return purpose; }
    public void setPurpose(PhoneOtpPurpose purpose) { this.purpose = purpose; }

    public String getPendingToken() { return pendingToken; }
    public void setPendingToken(String pendingToken) { this.pendingToken = pendingToken; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getUsedAt() { return usedAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static PhoneOtpBuilder builder() {
        return new PhoneOtpBuilder();
    }

    public static class PhoneOtpBuilder {
        private UUID id;
        private String phoneNumber;
        private String otp;
        private PhoneOtpPurpose purpose;
        private String pendingToken;
        private LocalDateTime expiresAt;
        private LocalDateTime usedAt;
        private LocalDateTime createdAt;

        public PhoneOtpBuilder id(UUID id) { this.id = id; return this; }
        public PhoneOtpBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public PhoneOtpBuilder otp(String otp) { this.otp = otp; return this; }
        public PhoneOtpBuilder purpose(PhoneOtpPurpose purpose) { this.purpose = purpose; return this; }
        public PhoneOtpBuilder pendingToken(String pendingToken) { this.pendingToken = pendingToken; return this; }
        public PhoneOtpBuilder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public PhoneOtpBuilder usedAt(LocalDateTime usedAt) { this.usedAt = usedAt; return this; }
        public PhoneOtpBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public PhoneOtp build() {
            return new PhoneOtp(id, phoneNumber, otp, purpose, pendingToken, expiresAt, usedAt, createdAt);
        }
    }
}
