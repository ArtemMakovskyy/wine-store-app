package com.winestoreapp.service.impl;

import com.winestoreapp.dto.mapper.UserMapper;
import com.winestoreapp.dto.user.UserRegistrationRequestDto;
import com.winestoreapp.dto.user.UserResponseDto;
import com.winestoreapp.exception.EntityNotFoundException;
import com.winestoreapp.exception.RegistrationException;
import com.winestoreapp.model.Role;
import com.winestoreapp.model.RoleName;
import com.winestoreapp.model.User;
import com.winestoreapp.repository.RoleRepository;
import com.winestoreapp.repository.UserRepository;
import com.winestoreapp.service.UserService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final int MINIMUM_ALLOWED_NUMBER_OF_ADMIN_USERS = 1;
    private static final long CUSTOMER_ROLE_ID = 3L;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException {
        if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationException("Unable to complete registration.");
        }

        Role roleCustomer = roleRepository.findById(CUSTOMER_ROLE_ID).orElseThrow(
                () -> new EntityNotFoundException("Can't find ROLE_CUSTOMER by id " + CUSTOMER_ROLE_ID));

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNumber(),
                Set.of(roleCustomer)
        );

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto updateRole(Long userId, String roleNameStr) {
        User userFromDb = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by id " + userId));

        Role roleFromDb = roleRepository.findByName(RoleName.valueOf(roleNameStr)).orElseThrow(
                () -> new EntityNotFoundException("Can't find role " + roleNameStr));

        // Використання доменного методу hasRole
        if (userFromDb.hasRole(RoleName.ROLE_ADMIN)) {
            List<User> adminsInDb = userRepository.findUsersByRole(RoleName.ROLE_ADMIN);
            if (adminsInDb.size() <= MINIMUM_ALLOWED_NUMBER_OF_ADMIN_USERS) {
                throw new RegistrationException("You cannot change the last admin role.");
            }
        }

        userFromDb.updateRoles(Set.of(roleFromDb));
        return userMapper.toDto(userRepository.save(userFromDb));
    }
}