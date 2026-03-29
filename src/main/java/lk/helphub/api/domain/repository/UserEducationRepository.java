package lk.helphub.api.domain.repository;

import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.entity.UserEducation;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.repository.query.Param;

import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserEducationRepository {
    void deleteByUser(@Param("user") User user);
    void delete(UserEducation entity);
    <S extends UserEducation> S save(S entity);
    <S extends UserEducation> Iterable<S> saveAll(Iterable<S> entities);
    java.util.Optional<UserEducation> findById(UUID id);
    java.util.List<UserEducation> findByUserId(UUID userId);
}
