package com.winestoreapp.service.impl;

import com.winestoreapp.dto.mapper.UserMapper;
import com.winestoreapp.dto.user.UserRegistrationRequestDto;
import com.winestoreapp.dto.user.UserResponseDto;
import com.winestoreapp.exception.RegistrationException;
import com.winestoreapp.model.Role;
import com.winestoreapp.model.RoleName;
import com.winestoreapp.model.User;
import com.winestoreapp.repository.RoleRepository;
import com.winestoreapp.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("New user registration with valid data, Return UserResponseDto")
    void register_ValidDto_ReturnUserResponseDto() throws RegistrationException {
        User user = getUser();
        UserRegistrationRequestDto requestDto = getUserRegistrationRequestDto(user, "");
        Role roleCustomer = new Role();
        ReflectionTestUtils.setField(roleCustomer, "name", RoleName.ROLE_CUSTOMER);

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded_pass");
        // В сервісі ROLE_CUSTOMER має ID 3L згідно з вашим verify
        when(roleRepository.findById(3L)).thenReturn(Optional.of(roleCustomer));
        when(userRepository.save(any())).thenReturn(user);

        UserResponseDto expected = toUserResponseDto(user);
        when(userMapper.toDto(any())).thenReturn(expected);

        UserResponseDto actual = userService.register(requestDto);

        assertEquals(expected, actual);
        verify(userRepository).findUserByEmail(requestDto.getEmail());
        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("Update role with user by valid data, Return UserResponseDto")
    void updateRole_ValidData_ReturnUserResponseDto() {
        // given
        User user = getUser();
        // Робимо користувача АДМІНОМ, щоб сервіс зайшов у блок перевірки findUsersByRole
        Role adminRole = new Role();
        ReflectionTestUtils.setField(adminRole, "name", RoleName.ROLE_ADMIN);
        ReflectionTestUtils.setField(user, "roles", Set.of(adminRole));

        Role managerRole = new Role();
        ReflectionTestUtils.setField(managerRole, "name", RoleName.ROLE_MANAGER);

        UserResponseDto expected = toUserResponseDto(user);
        expected.setRoles(Set.of(RoleName.ROLE_MANAGER.name()));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(RoleName.ROLE_MANAGER)).thenReturn(Optional.of(managerRole));

        // Мок для перевірки останнього адміна (нова логіка)
        when(userRepository.findUsersByRole(RoleName.ROLE_ADMIN)).thenReturn(List.of(user, new User()));

        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toDto(any())).thenReturn(expected);

        // when
        UserResponseDto actual = userService.updateRole(user.getId(), RoleName.ROLE_MANAGER.name());

        // then
        assertEquals(expected.getRoles(), actual.getRoles());
        verify(userRepository).findUsersByRole(RoleName.ROLE_ADMIN);
        verify(userRepository).save(any());
    }

    // --- Auxiliary Methods (Reflection based) ---
    private User getUser() {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 3L);
        ReflectionTestUtils.setField(user, "email", "test@email.com");
        ReflectionTestUtils.setField(user, "firstName", "Name");
        ReflectionTestUtils.setField(user, "lastName", "Last");
        ReflectionTestUtils.setField(user, "roles", new HashSet<>());
        return user;
    }

    private UserRegistrationRequestDto getUserRegistrationRequestDto(User user, String text) {
        UserRegistrationRequestDto dto = new UserRegistrationRequestDto();
        dto.setEmail(text + user.getEmail());
        dto.setPassword("pass");
        dto.setRepeatPassword("pass");
        return dto;
    }

    private UserResponseDto toUserResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setRoles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()));
        return dto;
    }
}
