package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.LoginOtp;
import lk.helphub.api.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface LoginOtpRepository {
    LoginOtp save(LoginOtp loginOtp);
    Optional<LoginOtp> findByOtpAndUser(String otp, User user);
    List<LoginOtp> findByUserAndUsedAtIsNull(User user);
}
