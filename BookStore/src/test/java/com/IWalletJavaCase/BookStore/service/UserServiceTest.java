package com.IWalletJavaCase.BookStore.service;

import static org.junit.jupiter.api.Assertions.*;

import com.IWalletJavaCase.BookStore.DTO.RegisterLoginUserDTO;
import com.IWalletJavaCase.BookStore.DTO.UserDTOMapper;
import com.IWalletJavaCase.BookStore.exception.UsernameAlreadyExistsException;
import com.IWalletJavaCase.BookStore.model.User;
import com.IWalletJavaCase.BookStore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private CreateCartService createCartService;
    @Mock
    private TokenService tokenService;
    @Mock
    private UserDTOMapper userDTOMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private RegisterLoginUserDTO userRequest;
    private String validUsername = "deneme";
    private String validPassword = "deneme";
    private Long validUserId = 1L;

    @BeforeEach
    void setUp() {
        user = new User(validUsername, validPassword);
        user.setId(validUserId);
        userRequest = new RegisterLoginUserDTO(validUsername, validPassword);
    }

    @Test
    void whenLoadUserByUsernameCalledWithValidUsername_itShouldReturnUserDetails() {
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.of(user));
        UserDetails userDetails = userService.loadUserByUsername(validUsername);
        assertEquals(user.getUsername(), userDetails.getUsername());
        verify(userRepository).findByUsername(validUsername);
    }

    @Test
    void whenLoadUserByUsernameCalledWithInvalidUsername_itShouldThrowUsernameNotFoundException() {
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(validUsername));
        verify(userRepository).findByUsername(validUsername);
    }
    @Test
    void whenLoadUserByUsernameCalledWithNullUsername_itShouldThrowUsernameNotFoundException() {
        String username = null;
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
        verify(userRepository, times(1)).findByUsername(username);
    }
    @Test
    void whenGetByUsernameCalledWithValidUsername_itShouldReturnUser() {
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.of(user));
        User foundUser = userService.getByUsername(validUsername);
        assertEquals(user, foundUser);
        verify(userRepository).findByUsername(validUsername);
    }

    @Test
    void whenGetByUsernameCalledWithInvalidUsername_itShouldThrowUsernameNotFoundException() {
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.getByUsername(validUsername));
        verify(userRepository).findByUsername(validUsername);
    }
    @Test
    void whenGetByUsernameCalledWithNullUsername_itShouldThrowUsernameNotFoundException() {
        String username = null;
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getByUsername(username));
        verify(userRepository, times(1)).findByUsername(username);
    }
    @Test
    void whenRegisterUserCalledWithValidRequest_itShouldReturnUserDTO() {
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validPassword)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userDTOMapper.convert(user)).thenReturn(new RegisterLoginUserDTO(user.getUsername(), user.getPassword()));

        RegisterLoginUserDTO savedUserDTO = userService.registerUser(userRequest);

        assertNotNull(savedUserDTO);
        verify(userRepository).findByUsername(validUsername);
        verify(passwordEncoder).encode(validPassword);
        verify(userRepository).save(any(User.class));
        verify(createCartService).createCart(user);
        verify(userDTOMapper).convert(user);
    }

    @Test
    void whenRegisterUserCalledWithExistingUsername_itShouldThrowUsernameAlreadyExistsException() {
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.of(user));
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.registerUser(userRequest));
        verify(userRepository).findByUsername(validUsername);
    }
    @Test
    void whenRegisterUserCalledWithNullRegisterLoginUserDTO_itShouldThrowNullPointerException() {
        RegisterLoginUserDTO registerLoginUserDTO = null;

        assertThrows(NullPointerException.class, () -> userService.registerUser(registerLoginUserDTO));
    }

    @Test
    void whenFindUserByUserIdCalledWithValidUserId_itShouldReturnUser() {
        when(userRepository.findById(validUserId)).thenReturn(Optional.of(user));
        User foundUser = userService.findUserbyUserId(validUserId);
        assertEquals(user, foundUser);
        verify(userRepository).findById(validUserId);
    }

    @Test
    void whenFindUserByUserIdCalledWithInvalidUserId_itShouldThrowIllegalArgumentException() {
        when(userRepository.findById(validUserId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.findUserbyUserId(validUserId));
        verify(userRepository).findById(validUserId);
    }
    @Test
    void whenFindUserByUserIdCalledWithNullUsername_itShouldThrowUsernameNotFoundException() {
        Long userId = null;
        when(userRepository.findById(validUserId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.findUserbyUserId(validUserId));
        verify(userRepository).findById(validUserId);
    }
    @Test
    void whenLogoutCalledWithValidAuthorizationHeader_itShouldInvalidateToken() {
        String token = "validToken";
        String authorizationHeader = "Bearer " + token;

        String result = userService.logout(authorizationHeader);

        assertEquals("Logged out successfully.", result);
        verify(tokenService).invalidateToken(token);
    }

    @Test
    void whenLogoutCalledWithInvalidAuthorizationHeader_itShouldNotInvalidateToken() {
        String authorizationHeader = "InvalidHeader";

        assertThrows(IllegalArgumentException.class, () -> userService.logout(authorizationHeader));
        verify(tokenService, never()).invalidateToken(anyString());
    }
    @Test
    void whenLogoutCalledWithNullUsername_itShouldThrowUsernameNotFoundException() {
        String authorizationHeader = null;

        assertThrows(IllegalArgumentException.class, () -> userService.logout(authorizationHeader));
        verify(tokenService, never()).invalidateToken(anyString());
    }
    @Test
    void whenFindUserIdByUsernameCalledWithValidUsername_itShouldReturnUserId() {
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.of(user));
        Long userId = userService.findUserIdbyUsername(validUsername);
        assertEquals(validUserId, userId);
        verify(userRepository).findByUsername(validUsername);
    }

    @Test
    void whenFindUserIdByUsernameCalledWithInvalidUsername_itShouldThrowUsernameNotFoundException() {
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.findUserIdbyUsername(validUsername));
        verify(userRepository).findByUsername(validUsername);
    }
    @Test
    void whenFindUserIdByUsernameCalledWithNullUsername_itShouldThrowUsernameNotFoundException() {
        String username = null;

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.findUserIdbyUsername(username));
        verify(userRepository).findByUsername(username);
    }
}
