package com.IWalletJavaCase.BookStore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenServiceTest {
    private TokenService tokenService;
    @BeforeEach
    void setUp() throws Exception{
        tokenService = new TokenService();
    }
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