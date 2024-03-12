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
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationException("Unable to complete registration.");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        Role roleUser =
                roleRepository.findById(3L).orElseThrow(
                        () -> new EntityNotFoundException("Can't find ROLE_CUSTOMER by id"));
        user.setRoles(Set.of(roleUser));

        final User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDto updateRole(Long userId, String role) {
        final User userFromDb = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by id " + userId));
        final Role roleFromDb = roleRepository.findByName(RoleName.valueOf(role)).orElseThrow(
                () -> new EntityNotFoundException("Can't find role " + role));
        userFromDb.setRoles(Set.of(roleFromDb));
        return userMapper.toDto(userRepository.save(userFromDb));
    }
}
