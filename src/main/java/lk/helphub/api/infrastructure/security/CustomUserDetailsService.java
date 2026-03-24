package lk.helphub.api.infrastructure.security;

import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByPhoneNumber(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email or phone: " + identifier));

        String username = (user.getEmail() != null) ? user.getEmail() : user.getPhoneNumber();

        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(user.getPasswordHash())
                .authorities(user.getRoles().stream()
                        .flatMap(role -> {
                            Stream<SimpleGrantedAuthority> roleAuth = Stream.of(
                                    new SimpleGrantedAuthority("ROLE_" + role.getName()));
                            Stream<SimpleGrantedAuthority> permAuth = role.getPermissions().stream()
                                    .map(permission -> new SimpleGrantedAuthority(permission.getSlug()));
                            return Stream.concat(roleAuth, permAuth);
                        })
                        .collect(Collectors.toList()))
                .disabled("inactive".equals(user.getStatus()))
                .build();
    }
}

