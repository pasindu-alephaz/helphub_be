package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.UserAddressRequest;
import lk.helphub.api.application.dto.UserAddressResponse;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.entity.UserAddress;
import lk.helphub.api.domain.repository.UserAddressRepository;
import lk.helphub.api.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserAddressResponse> getAddresses(String email) {
        User user = findUser(email);
        return userAddressRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserAddressResponse addAddress(String email, UserAddressRequest request) {
        User user = findUser(email);

        // If new address is default, clear any existing default first
        if (request.isDefault()) {
            clearCurrentDefault(user.getId());
        }

        UserAddress address = UserAddress.builder()
                .user(user)
                .label(request.getLabel())
                .province(request.getProvince())
                .district(request.getDistrict())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isDefault(request.isDefault())
                .build();

        return mapToResponse(userAddressRepository.save(address));
    }

    @Transactional
    public UserAddressResponse updateAddress(String email, UUID addressId, UserAddressRequest request) {
        User user = findUser(email);
        UserAddress address = userAddressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        if (request.isDefault()) {
            clearCurrentDefault(user.getId());
        }

        address.setLabel(request.getLabel());
        address.setProvince(request.getProvince());
        address.setDistrict(request.getDistrict());
        address.setCity(request.getCity());
        address.setPostalCode(request.getPostalCode());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setDefault(request.isDefault());

        return mapToResponse(userAddressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(String email, UUID addressId) {
        User user = findUser(email);
        UserAddress address = userAddressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
        userAddressRepository.delete(address);
    }

    @Transactional
    public UserAddressResponse setDefault(String email, UUID addressId) {
        User user = findUser(email);
        clearCurrentDefault(user.getId());

        UserAddress address = userAddressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
        address.setDefault(true);
        return mapToResponse(userAddressRepository.save(address));
    }

    private void clearCurrentDefault(UUID userId) {
        userAddressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(existing -> {
                    existing.setDefault(false);
                    userAddressRepository.save(existing);
                });
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .or(() -> userRepository.findByPhoneNumber(email))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private UserAddressResponse mapToResponse(UserAddress a) {
        return UserAddressResponse.builder()
                .id(a.getId())
                .label(a.getLabel())
                .province(a.getProvince())
                .district(a.getDistrict())
                .city(a.getCity())
                .postalCode(a.getPostalCode())
                .latitude(a.getLatitude())
                .longitude(a.getLongitude())
                .isDefault(a.isDefault())
                .build();
    }
}
