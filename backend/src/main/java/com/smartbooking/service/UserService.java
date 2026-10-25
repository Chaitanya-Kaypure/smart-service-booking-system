package com.smartbooking.service;

import com.smartbooking.dto.user.ChangePasswordRequest;
import com.smartbooking.dto.user.UpdateProfileRequest;
import com.smartbooking.dto.user.UserResponse;
import com.smartbooking.entity.Role;
import com.smartbooking.entity.User;
import com.smartbooking.exception.BadRequestException;
import com.smartbooking.exception.ForbiddenException;
import com.smartbooking.exception.ResourceNotFoundException;
import com.smartbooking.mapper.UserMapper;
import com.smartbooking.repository.UserRepository;
import com.smartbooking.security.SecurityUtils;
import com.smartbooking.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        return userMapper.toResponse(getUserEntity(SecurityUtils.currentUser().getId()));
    }

    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        UserPrincipal principal = SecurityUtils.currentUser();
        User user = getUserEntity(principal.getId());
        user.setFullName(request.getFullName().trim());
        user.setPhone(request.getPhone());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        UserPrincipal principal = SecurityUtils.currentUser();
        User user = getUserEntity(principal.getId());
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> listCustomers(String search, Pageable pageable) {
        return userRepository.searchByRole(Role.CUSTOMER, search, pageable)
                .map(userMapper::toResponse);
    }

    @Transactional
    public UserResponse setCustomerActive(Long userId, boolean active) {
        User user = getUserEntity(userId);
        if (user.getRole() != Role.CUSTOMER) {
            throw new ForbiddenException("Only customer accounts can be updated through this endpoint");
        }
        user.setActive(active);
        return userMapper.toResponse(userRepository.save(user));
    }

    User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
