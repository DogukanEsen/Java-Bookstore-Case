package com.IWalletJavaCase.BookStore.service;

import com.IWalletJavaCase.BookStore.DTO.RegisterLoginUserDTO;
import com.IWalletJavaCase.BookStore.security.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class LoginUserServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private LoginUserService loginUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenLoginUserCalledWithValidCredentials_itShouldReturnToken() {
        RegisterLoginUserDTO request = new RegisterLoginUserDTO("testUser", "password");
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        Mockito.when(jwtTokenUtil.generateToken("testUser")).thenReturn("generatedToken");

        String token = loginUserService.loginUser(request);

        assertNotNull(token);
        assertEquals("generatedToken", token);
    }

    @Test
    void whenLoginUserCalledWithInvalidCredentials_itShouldThrowUsernameNotFoundException() {
        RegisterLoginUserDTO request = new RegisterLoginUserDTO("invalidUser", "invalidPassword");
        Authentication authentication =Mockito. mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(false);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        assertThrows(UsernameNotFoundException.class, () -> loginUserService.loginUser(request));
    }

    @Test
    void whenLoginUserCalledWithNullCredentials_itShouldThrowIllegalArgumentException() {
        RegisterLoginUserDTO request = new RegisterLoginUserDTO(null, null);

        assertThrows(IllegalArgumentException.class, () -> loginUserService.loginUser(request));
        Mockito.verifyNoInteractions(authenticationManager);
        Mockito.verifyNoInteractions(jwtTokenUtil);
    }
}