package com.winestoreapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final Long ROLE_ID = 2L;
    private static final String ROLE_MANAGER = "ROLE_MANAGER";
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

    @DisplayName("New user registration with valid data, Return UserResponseDto")
    @Test
    void register_ValidDto_ReturnUserResponseDto() throws RegistrationException {
        // given
        UserRegistrationRequestDto requestDto =
                getUserRegistrationRequestDto(getUser(), "");
        User newUser = getUser();
        Role roleManager = new Role();
        roleManager.setName(RoleName.ROLE_MANAGER);

        when(userRepository.findUserByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn(newUser.getPassword());
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(roleManager));
        when(userRepository.save(any())).thenReturn(newUser);
        UserResponseDto expected = toUserResponseDto(newUser);
        when(userMapper.toDto(any())).thenReturn(expected);

        // when
        UserResponseDto actual = userService.register(requestDto);

        // then
        assertEquals(expected, actual);
        verify(userRepository, times(1)).findUserByEmail(requestDto.getEmail());
        verify(roleRepository, times(1)).findById(3L);
        verify(userRepository, times(1)).save(any());
        verify(userMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper);
    }

    @DisplayName("Update role with user by valid data, Return UserResponseDto")
    @Test
    void updateRole_ValidData_ReturnUserResponseDto() {
        //given
        UserResponseDto expected = toUserResponseDto(getUser());
        expected.setRoles(Set.of(ROLE_MANAGER));

        Role roleEntity = new Role();
        roleEntity.setId(ROLE_ID);
        roleEntity.setName(RoleName.ROLE_MANAGER);
        User user = getUser();

        User userAfterUpdatingRole = getUserAfterUpdatingRole(user);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(any())).thenReturn(Optional.of(roleEntity));
        when(userRepository.findUsersByRole(RoleName.ROLE_ADMIN)).thenReturn(List.of(user));
        when(userRepository.save(any())).thenReturn(userAfterUpdatingRole);
        when(userMapper.toDto(any())).thenReturn(expected);

        //when
        UserResponseDto actual = userService.updateRole(user.getId(), ROLE_MANAGER);

        //then
        assertEquals(user.getId(), actual.getId());
        assertEquals(ROLE_MANAGER, actual.getRoles().iterator().next());
        verify(userRepository, times(1)).findById(user.getId());
        verify(roleRepository, times(1)).findByName(any());
        verify(userRepository,
                times(1)).findUsersByRole(any());
        verify(userRepository, times(1)).save(any());
        verify(userMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper);
    }

    private UserRegistrationRequestDto getUserRegistrationRequestDto(
            User user, String addTextToAllNeedFields) {
        UserRegistrationRequestDto requestDto =
                new UserRegistrationRequestDto();
        requestDto.setEmail(addTextToAllNeedFields + user.getEmail());
        requestDto.setFirstName(addTextToAllNeedFields + user.getFirstName());
        requestDto.setLastName(addTextToAllNeedFields + user.getLastName());
        requestDto.setPassword(addTextToAllNeedFields + user.getPassword());
        requestDto.setRepeatPassword(addTextToAllNeedFields + user.getPassword());
        return requestDto;
    }

    private User getUserAfterUpdatingRole(User user) {
        User userAfterUpdatingRole = getUser();
        Role roleManager = new Role();
        roleManager.setName(RoleName.ROLE_MANAGER);
        Set<Role> roles = new HashSet<>();
        roles.add(roleManager);
        userAfterUpdatingRole.setRoles(roles);
        return userAfterUpdatingRole;
    }

    private UserResponseDto toUserResponseDto(User user) {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(user.getId());
        responseDto.setFirstName(user.getFirstName());
        responseDto.setLastName(user.getLastName());
        responseDto.setEmail(user.getEmail());
        responseDto.setRoles(
                user.getRoles().stream()
                        .map(s -> s.getName().name())
                        .collect(Collectors.toSet()));
        return responseDto;
    }

    private User getUser() {
        Role roleCustomer = new Role();
        roleCustomer.setName(RoleName.ROLE_CUSTOMER);
        Set<Role> roles = new HashSet<>();
        roles.add(roleCustomer);
        User user = new User();
        user.setId(3L);
        user.setEmail("user1234@email.com");
        user.setFirstName("UserFirstName");
        user.setLastName("UserLastName");
        user.setPassword("1234");
        user.setPhoneNumber("+380501234569");
        user.setRoles(roles);
        user.setDeleted(false);
        return user;
    }
}
