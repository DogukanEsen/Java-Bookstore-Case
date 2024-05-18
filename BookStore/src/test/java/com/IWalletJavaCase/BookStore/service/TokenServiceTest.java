package com.IWalletJavaCase.BookStore.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
@ExtendWith(MockitoExtension.class)

class TokenServiceTest {
    @InjectMocks
    private TokenService tokenService;

    @Test
    void whenInvalidateTokenCalledWithValidToken_itShouldAddTokenToInvalidatedSet() {
        String token = "token123";

        assertFalse(tokenService.isTokenInvalidated(token));

        tokenService.invalidateToken(token);

        assertTrue(tokenService.isTokenInvalidated(token));
    }

    @Test
    void whenIsTokenInvalidatedCalledWithInvalidatedToken_itShouldReturnTrue() {
        String token = "invalidatedToken";

        tokenService.invalidateToken(token);

        assertTrue(tokenService.isTokenInvalidated(token));
    }
    @Test
    void whenIsTokenInvalidatedCalledWithNonInvalidatedToken_itShouldReturnFalse() {
        String token = "validToken";

        assertFalse(tokenService.isTokenInvalidated(token));
    }

}