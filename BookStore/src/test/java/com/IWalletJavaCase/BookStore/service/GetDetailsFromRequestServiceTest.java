package com.IWalletJavaCase.BookStore.service;

import com.IWalletJavaCase.BookStore.security.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GetDetailsFromRequestServiceTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @Mock
    private UserService userService;
    @Mock
    private HttpServletRequest request;
    @InjectMocks
    private GetDetailsFromRequestService getDetailsFromRequestService;
    @BeforeEach
    void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    void whenGetUsernameFromJwtCalledWithValidToken_itShouldReturnUsername() {
        String token = "dummyToken";
        String username = "testUser";
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        Mockito.when(jwtTokenUtil.extractUsername(token)).thenReturn(username);

        String extractedUsername = getDetailsFromRequestService.getUsernameFromJwt(request);

        assertEquals(username, extractedUsername);
        Mockito.verify(jwtTokenUtil, Mockito.times(1)).extractUsername(token);
    }

    @Test
    void whenGetUsernameFromJwtCalledWithNoAuthorizationHeader_itShouldThrowUsernameNotFoundException() {
        Mockito.when(request.getHeader("Authorization")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> getDetailsFromRequestService.getUsernameFromJwt(request));
        Mockito.verify(jwtTokenUtil, Mockito.never()).extractUsername(Mockito.anyString());
    }

    @Test
    void whenGetUsernameFromJwtCalledWithInvalidAuthorizationHeader_itShouldThrowUsernameNotFoundException() {
        Mockito.when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        assertThrows(UsernameNotFoundException.class, () -> getDetailsFromRequestService.getUsernameFromJwt(request));
        Mockito.verify(jwtTokenUtil, Mockito.never()).extractUsername(Mockito.anyString());
    }

    @Test
    void whenGetUsernameFromJwtCalledWithTokenThatReturnsNullFromJwtTokenUtil_itShouldThrowUsernameNotFoundException() {
        String token = "dummyToken";
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        Mockito.when(jwtTokenUtil.extractUsername(token)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> getDetailsFromRequestService.getUsernameFromJwt(request));
        Mockito.verify(jwtTokenUtil, Mockito.times(1)).extractUsername(token);
    }

    @Test
    void whenGetUserIdFromJwtCalledWithValidToken_itShouldReturnUserId() {
        String token = "dummyToken";
        String username = "testUser";
        Long userId = 1L;
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        Mockito.when(jwtTokenUtil.extractUsername(token)).thenReturn(username);
        Mockito.when(userService.findUserIdbyUsername(username)).thenReturn(userId);

        Long extractedUserId = getDetailsFromRequestService.getUserIdFromJwt(request);

        assertEquals(userId, extractedUserId);
        Mockito.verify(jwtTokenUtil, Mockito.times(1)).extractUsername(token);
        Mockito.verify(userService, Mockito.times(1)).findUserIdbyUsername(username);
    }

    @Test
    void whenGetUserIdFromJwtCalledWithInvalidToken_itShouldThrowUsernameNotFoundException() {
        Mockito.when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        assertThrows(UsernameNotFoundException.class, () -> getDetailsFromRequestService.getUserIdFromJwt(request));
        Mockito.verify(jwtTokenUtil, Mockito.never()).extractUsername(Mockito.anyString());
        Mockito.verify(userService, Mockito.never()).findUserIdbyUsername(Mockito.anyString());
    }

    @Test
    void whenGetUserIdFromJwtCalledWithUsernameNotFound_itShouldReturnNull() {
        String token = "dummyToken";
        String username = "testUser";
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        Mockito.when(jwtTokenUtil.extractUsername(token)).thenReturn(username);
        Mockito.when(userService.findUserIdbyUsername(username)).thenReturn(null);

        Long extractedUserId = getDetailsFromRequestService.getUserIdFromJwt(request);

        assertNull(extractedUserId);
        Mockito.verify(jwtTokenUtil, Mockito.times(1)).extractUsername(token);
        Mockito.verify(userService, Mockito.times(1)).findUserIdbyUsername(username);
    }
}