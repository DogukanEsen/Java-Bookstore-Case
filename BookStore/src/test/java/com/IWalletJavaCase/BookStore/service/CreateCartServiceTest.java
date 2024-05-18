package com.IWalletJavaCase.BookStore.service;

import com.IWalletJavaCase.BookStore.model.Cart;
import com.IWalletJavaCase.BookStore.model.User;
import com.IWalletJavaCase.BookStore.repository.CartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class CreateCartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CreateCartService createCartService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenCreateCartCalledWithValidUser_itShouldReturnCart() {
        User user = new User();
        user.setId(1L);

        Cart expectedCart = new Cart();
        expectedCart.setUser(user);

        Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenReturn(expectedCart);

        Cart createdCart = createCartService.createCart(user);

        Assertions.assertNotNull(createdCart);
        Assertions.assertEquals(user, createdCart.getUser());
        Mockito.verify(cartRepository, Mockito.times(1)).save(Mockito.any(Cart.class));
    }
    @Test
    public void whenCreateCartCalledWithInvalidUser_itShouldThrowIllegalArgumentException() {
        User invalidUser = null;

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            createCartService.createCart(invalidUser);
        });
    }
    @Test
    public void whenCreateCartCalledWithNullUser_itShouldThrowIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            createCartService.createCart(null);
        });
    }
}