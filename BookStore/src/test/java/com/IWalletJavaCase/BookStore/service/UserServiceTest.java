package com.IWalletJavaCase.BookStore.service;

import static org.junit.jupiter.api.Assertions.*;

import com.IWalletJavaCase.BookStore.DTO.RegisterLoginUserDTO;
import com.IWalletJavaCase.BookStore.DTO.UserDTOMapper;
import com.IWalletJavaCase.BookStore.exception.UsernameAlreadyExistsException;
import com.IWalletJavaCase.BookStore.model.User;
import com.IWalletJavaCase.BookStore.repository.UserRepository;
import com.IWalletJavaCase.BookStore.security.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private CreateCartService createCartService;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserDTOMapper userDTOMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenLoadUserByUsernameCalledWithValidUsername_itShouldReturnUserDetails() {
        User user = new User("testUser", "password");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("testUser");

        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
    }

    @Test
    void whenLoadUserByUsernameCalledWithInvalidUsername_itShouldThrowUsernameNotFoundException() {
        when(userRepository.findByUsername("invalidUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("invalidUser"));
    }

    @Test
    void whenGetByUsernameCalledWithValidUsername_itShouldReturnUser() {
        User user = new User("testUser", "password");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        User foundUser = userService.getByUsername("testUser");

        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
    }

    @Test
    void whenGetByUsernameCalledWithInvalidUsername_itShouldThrowUsernameNotFoundException() {
        when(userRepository.findByUsername("invalidUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("invalidUser"));
    }

    @Test
    void whenRegisterUserCalledWithValidRequest_itShouldReturnRegisterLoginUserDTO() {
        RegisterLoginUserDTO request = new RegisterLoginUserDTO("newUser", "password");
        User user = new User("newUser", "encodedPassword");
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userDTOMapper.convert(any(User.class))).thenReturn(request);

        RegisterLoginUserDTO response = userService.registerUser(request);

        assertNotNull(response);
        assertEquals("newUser", response.username());
        verify(createCartService, times(1)).createCart(user);
    }

    @Test
    void whenRegisterUserCalledWithExistingUsername_itShouldThrowUsernameAlreadyExistsException() {
        RegisterLoginUserDTO request = new RegisterLoginUserDTO("existingUser", "password");
        User user = new User("existingUser", "password");
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(user));

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.registerUser(request));
    }

    @Test
    void whenGetUsernameFromRequestCalledWithValidToken_itShouldReturnUsername() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtTokenUtil.extractUsername("validToken")).thenReturn("testUser");

        String username = userService.getUsernameFromRequest(request);

        assertEquals("testUser", username);
    }

    @Test
    void whenGetUsernameFromRequestCalledWithInvalidToken_itShouldReturnNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtTokenUtil.extractUsername("invalidToken")).thenReturn(null);

        String username = userService.getUsernameFromRequest(request);

        assertNull(username);
    }

    @Test
    void whenFindUserByUserIdCalledWithValidUserId_itShouldReturnUser() {
        User user = new User("testUser", "password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.findUserbyUserId(1L);

        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
    }

    @Test
    void whenFindUserByUserIdCalledWithInvalidUserId_itShouldThrowIllegalArgumentException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.findUserbyUserId(userId));
    }

    @Test
    void whenLogoutCalledWithValidAuthorizationHeader_itShouldInvalidateToken() {
        String validToken = "validToken";
        String authorizationHeader = "Bearer " + validToken;

        String response = userService.logout(authorizationHeader);

        assertEquals("Logged out successfully.", response);
        verify(tokenService, times(1)).invalidateToken(validToken);
    }

    @Test
    void whenLogoutCalledWithInvalidAuthorizationHeader_itShouldNotInvalidateToken() {
        String invalidAuthorizationHeader = "InvalidHeader";

        String response = userService.logout(invalidAuthorizationHeader);

        assertEquals("Logged out successfully.", response);
        verify(tokenService, never()).invalidateToken(anyString());
    }
}
