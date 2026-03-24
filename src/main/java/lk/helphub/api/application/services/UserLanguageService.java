package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.UserLanguageRequest;
import lk.helphub.api.application.dto.UserLanguageResponse;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.entity.UserLanguage;
import lk.helphub.api.domain.repository.UserLanguageRepository;
import lk.helphub.api.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLanguageService {

    private final UserLanguageRepository userLanguageRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserLanguageResponse> getLanguages(String email) {
        User user = findUser(email);
        return userLanguageRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserLanguageResponse addLanguage(String email, UserLanguageRequest request) {
        User user = findUser(email);

        UserLanguage language = UserLanguage.builder()
                .user(user)
                .languageCode(request.getLanguageCode())
                .languageName(request.getLanguageName())
                .proficiency(request.getProficiency())
                .build();

        return mapToResponse(userLanguageRepository.save(language));
    }

    @Transactional
    public UserLanguageResponse updateLanguage(String email, UUID languageId, UserLanguageRequest request) {
        User user = findUser(email);
        UserLanguage language = userLanguageRepository.findByIdAndUserId(languageId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Language entry not found"));

        language.setLanguageCode(request.getLanguageCode());
        language.setLanguageName(request.getLanguageName());
        language.setProficiency(request.getProficiency());

        return mapToResponse(userLanguageRepository.save(language));
    }

    @Transactional
    public void deleteLanguage(String email, UUID languageId) {
        User user = findUser(email);
        UserLanguage language = userLanguageRepository.findByIdAndUserId(languageId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Language entry not found"));
        userLanguageRepository.delete(language);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .or(() -> userRepository.findByPhoneNumber(email))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private UserLanguageResponse mapToResponse(UserLanguage l) {
        return UserLanguageResponse.builder()
                .id(l.getId())
                .languageCode(l.getLanguageCode())
                .languageName(l.getLanguageName())
                .proficiency(l.getProficiency())
                .build();
    }
}
