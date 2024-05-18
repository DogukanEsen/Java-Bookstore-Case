package com.IWalletJavaCase.BookStore.service;

import com.IWalletJavaCase.BookStore.DTO.CartItemDTO;
import com.IWalletJavaCase.BookStore.DTO.CartItemDTOMapper;
import com.IWalletJavaCase.BookStore.model.Book;
import com.IWalletJavaCase.BookStore.model.Cart;
import com.IWalletJavaCase.BookStore.model.CartItem;
import com.IWalletJavaCase.BookStore.model.User;
import com.IWalletJavaCase.BookStore.repository.CartItemRepository;
import com.IWalletJavaCase.BookStore.repository.CartRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.mockito.quality.Strictness;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookService bookService;
    @Mock
    private CartItemDTOMapper cartItemDTOMapper;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private User user;
    private Book book;
    private CartItem cartItem;
    private CartItemDTO cartItemDTO;
    private CartItemDTO cartItemDTO2;

    @BeforeEach
    void setUp() {
        user = new User("username", "password");
        user.setId(1L);

        cart = new Cart(user);
        cart.setId(1L);

        book = new Book("123", "title", "description", 5.0, 5);

        cartItem = new CartItem(book, 2, cart);
        cartItem.setId(1L);

        cartItemDTO = new CartItemDTO("isbn123", 2);
        cartItemDTO2 = new CartItemDTO("123", 2);
    }

    @Test
    void whenAddBookToCartCalledWithValidRequest_itShouldReturnUpdatedCartItems() {
        Mockito.when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepository.findAllByCart(cart)).thenReturn(List.of(cartItem));
        Mockito.when(bookService.findBookByIsbn(cartItemDTO.bookIsbn())).thenReturn(book);
        Mockito.when(cartItemDTOMapper.convert(Mockito.any(CartItem.class))).thenReturn(cartItemDTO);

        List<CartItemDTO> cartItems = cartService.addBookToCart(user.getId(), cartItemDTO);

        assertFalse(cartItems.isEmpty());
        Mockito.verify(cartRepository).findByUserId(user.getId());
        Mockito.verify(cartItemRepository).save(Mockito.any(CartItem.class));
    }

    @Test
    void whenAddBookToCartCalledWithInvalidIsbn_itShouldThrowException() {
        Mockito.when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        Mockito.when(bookService.findBookByIsbn("invalidIsbn")).thenThrow(new IllegalArgumentException("Book not found"));

        CartItemDTO invalidIsbnCartItemDTO = new CartItemDTO("invalidIsbn", 1);

        assertThrows(IllegalArgumentException.class, () -> cartService.addBookToCart(user.getId(), invalidIsbnCartItemDTO));
    }
    @Test
    void whenAddBookToCartCalledWithInvalidUserId_itShouldThrowException() {
        Mockito.when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());

        CartItemDTO cartItemDTO = new CartItemDTO("isbn123", 1);

        assertThrows(IllegalArgumentException.class, () -> cartService.addBookToCart(999L, cartItemDTO));
    }
    @Test
    void whenAddBookToCartCalledWithNegativeQuantity_itShouldThrowException() {
        Mockito.when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        Mockito.when(bookService.findBookByIsbn(cartItemDTO.bookIsbn())).thenReturn(book);

        CartItemDTO negativeQuantityCartItemDTO = new CartItemDTO("isbn123", -1);

        assertThrows(IllegalArgumentException.class, () -> cartService.addBookToCart(user.getId(), negativeQuantityCartItemDTO));
    }
    @Test
    void whenAddBookToCartCalledWithInsufficientStock_itShouldThrowException() {
        Mockito.when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        Mockito.when(bookService.findBookByIsbn(cartItemDTO.bookIsbn())).thenReturn(book);

        CartItemDTO insufficientStockCartItemDTO = new CartItemDTO("isbn123", 11);

        assertThrows(IllegalArgumentException.class, () -> cartService.addBookToCart(user.getId(), insufficientStockCartItemDTO));
    }
    @Test
    public void whenAddBookToCartCalledWithNullCartItemDTOBookIsbn_itShouldThrowException() {
        Cart cart = new Cart();
        CartItemDTO cartItemDTO = new CartItemDTO(null, 2);

        assertThrows(IllegalArgumentException.class, () -> cartService.addBookToCart(user.getId(), cartItemDTO));
    }
    @Test
    void whenAddBookToCartCalledWithNullCartItemDTO_itShouldThrowException() {
        CartItemDTO cartItemDTO1 = null;

        assertThrows(IllegalArgumentException.class, () -> {
            cartService.addBookToCart(user.getId(), cartItemDTO1);
        });
    }
    @Test
    void whenAddBookToCartCalledWithNullUserId_itShouldThrowException() {
        Long nullUserId = null;

        assertThrows(IllegalArgumentException.class, () -> {
            cartService.addBookToCart(nullUserId,cartItemDTO);
        });
    }
    @Test
    void whenDeleteBookToCartCalledWithValidRequest_itShouldReturnUpdatedCartItems() {
        Mockito.when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepository.findAllByCart(cart)).thenReturn(List.of(cartItem));
        Mockito.when(bookService.findBookByIsbn(cartItemDTO.bookIsbn())).thenReturn(book);
        Mockito.when(cartItemDTOMapper.convert(Mockito.any(CartItem.class))).thenReturn(cartItemDTO);

        List<CartItemDTO> cartItems = cartService.deleteBookToCart(user.getId(), cartItemDTO);

        assertFalse(cartItems.isEmpty());
        Mockito.verify(cartRepository).findByUserId(user.getId());
    }
    @Test
    void whenDeleteBookToCartCalledWithInvalidIsbn_itShouldThrowException() {
        Mockito.when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        Mockito.when(bookService.findBookByIsbn("invalidIsbn")).thenThrow(new IllegalArgumentException("Book not found"));

        CartItemDTO invalidIsbnCartItemDTO = new CartItemDTO("invalidIsbn", 1);

        assertThrows(IllegalArgumentException.class, () -> cartService.deleteBookToCart(user.getId(), invalidIsbnCartItemDTO));
    }
    @Test
    void whenDeleteBookToCartCalledWithInvalidUserId_itShouldThrowException() {
        Mockito.when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());

        CartItemDTO cartItemDTO = new CartItemDTO("isbn123", 1);

        assertThrows(IllegalArgumentException.class, () -> cartService.deleteBookToCart(999L, cartItemDTO));
    }
    @Test
    void whenDeleteBookToCartCalledWithNegativeQuantity_itShouldThrowException() {
        CartItemDTO positiveQuantityCartItemDTO = new CartItemDTO("isbn123", -1);

        assertThrows(IllegalArgumentException.class, () -> cartService.deleteBookToCart(user.getId(), positiveQuantityCartItemDTO));
    }
    @Test
    void whenDeleteBookToCartCalledWithExcessiveRemoval_itShouldThrowException() {
        Mockito.when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepository.findAllByCart(cart)).thenReturn(List.of(cartItem));

        CartItemDTO excessiveRemovalDTO = new CartItemDTO("isbn123", 3);

        assertThrows(IllegalArgumentException.class, () -> cartService.deleteBookToCart(user.getId(), excessiveRemovalDTO));
    }
    @Test
    public void whenDeleteBookToCartCalledWithNullCartItemDTOBookIsbn_itShouldThrowException() {
        Cart cart = new Cart();
        CartItemDTO cartItemDTO = new CartItemDTO(null, 2);

        assertThrows(IllegalArgumentException.class, () -> cartService.deleteBookToCart(user.getId(), cartItemDTO));
    }
    @Test
    void whenDeleteBookToCartCalledWithNullCartItemDTO_itShouldThrowException() {
        CartItemDTO cartItemDTO1 = null;

        assertThrows(NullPointerException.class, () -> {
            cartService.deleteBookToCart(user.getId(), cartItemDTO1);
        });
    }
    @Test
    void whenDeleteBookToCartCalledWithNullUserId_itShouldThrowException() {
        Long nullUserId = null;

        assertThrows(IllegalArgumentException.class, () -> {
            cartService.checkPayment(nullUserId);
        });
    }
    @Test
    void whenGetCartItemsCalledWithValidUserId_itShouldReturnCartItems() {
        Mockito.when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepository.findAllByCart(cart)).thenReturn(List.of(cartItem));
        Mockito.when(cartItemDTOMapper.convert(Mockito.any(CartItem.class))).thenReturn(cartItemDTO);

        List<CartItemDTO> cartItems = cartService.getCartItems(user.getId());

        assertFalse(cartItems.isEmpty());
        Mockito.verify(cartRepository).findByUserId(user.getId());
    }
    @Test
    void whenGetCartItemsCalledWithInvalidUserId_itShouldThrowException() {
        Long invalidUserId = 999L;

        Mockito.when(cartRepository.findByUserId(invalidUserId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.getCartItems(invalidUserId);
        });

        assertEquals("Sepet bulunamadı", exception.getMessage());
        Mockito.verify(cartRepository).findByUserId(invalidUserId);
    }
    @Test
    void whenCheckPaymentCalledWithValidUserId_itShouldReturnTotalPrice() {
        Mockito.when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        Mockito.when(cartItemRepository.findAllByCart(cart)).thenReturn(List.of(cartItem));

        String totalPrice = cartService.checkPayment(user.getId());

        assertEquals("10.0", totalPrice);
        Mockito.verify(cartRepository).findByUserId(user.getId());
        Mockito.verify(cartItemRepository).delete(cartItem);
    }
    @Test
    void whenCheckPaymentCalledWithInvalidUserId_itShouldThrowException() {
        Long invalidUserId = 999L;

        Mockito.when(cartRepository.findByUserId(invalidUserId)).thenReturn(Optional.empty());


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.checkPayment(invalidUserId);
        });


        assertEquals("Sepet bulunamadı", exception.getMessage());
        Mockito.verify(cartRepository).findByUserId(invalidUserId);
    }

    @Test
    void whenCheckPaymentCalledWithNullUserId_itShouldThrowException() {
        Long nullUserId = null;

        assertThrows(IllegalArgumentException.class, () -> {
            cartService.checkPayment(nullUserId);
        });
    }

    @Test
    public void whenFindAndUpdateCartItemCalledWithValidCart_itShouldUpdateExistingCartItem() {
        Mockito.when(cartItemRepository.findAllByCart(cart)).thenReturn(List.of(cartItem));
        Mockito.when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        Mockito.when(bookService.findBookByIsbn(cartItemDTO2.bookIsbn())).thenReturn(book);

        CartItem updatedItem = cartService.findAndUpdateCartItem(cart, cartItemDTO2);

        assertEquals(4, updatedItem.getQuantity());
        Mockito.verify(cartItemRepository, Mockito.times(1)).save(cartItem);
        Mockito.verify(bookService, Mockito.times(1)).findBookByIsbn(cartItemDTO2.bookIsbn());
    }

    @Test
    public void whenFindAndUpdateCartItemCalledWithInvalidCart_itShouldReturnNull() {
        Cart cart = null;
        CartItemDTO cartItemDTO = new CartItemDTO("1234567890", 2);

        CartItem updatedCartItem = cartService.findAndUpdateCartItem(cart, cartItemDTO);

        assertNull(updatedCartItem);
    }

    @Test
    public void whenFindAndUpdateCartItemCalledWithNullCart_itShouldReturnNull() {
        Cart cart = null;
        CartItemDTO cartItemDTO = new CartItemDTO("1234567890", 2);


        CartItem updatedCartItem = cartService.findAndUpdateCartItem(cart, cartItemDTO);


        assertNull(updatedCartItem);
    }

    @Test
    public void whenFindAndUpdateCartItemCalledWithNullCartItemDTOBookIsbn_itShouldReturnNull() {
        Cart cart = new Cart();
        CartItemDTO cartItemDTO = new CartItemDTO(null, 2);

        CartItem updatedCartItem = cartService.findAndUpdateCartItem(cart, cartItemDTO);

        assertNull(updatedCartItem);
    }

    @Test
    public void whenFindAndUpdateCartItemCalledWithNullCartItemDTO_itShouldReturnNull() {
        Cart cart = new Cart();
        CartItemDTO cartItemDTO = null;

        CartItem updatedCartItem = cartService.findAndUpdateCartItem(cart, cartItemDTO);

        assertNull(updatedCartItem);
    }

    @Test
    void whenCheckStockCalledWithValidStock_itShouldReturnTrue() {
        Mockito.when(bookService.findBookByIsbn(book.getIsbn())).thenReturn(book);

        boolean hasStock = cartService.checkStock(book.getQuantity(), book.getIsbn());

        assertTrue(hasStock);
        Mockito.verify(bookService, Mockito.times(1)).findBookByIsbn(book.getIsbn());
    }

    @Test
    void whenCheckStockCalledWithInvalidStock_itShouldReturnFalse() {
        int quantity = 100;
        Mockito.when(bookService.findBookByIsbn(book.getIsbn())).thenReturn(book);
        boolean hasStock = cartService.checkStock(quantity, book.getIsbn());

        assertFalse(hasStock);
        Mockito.verify(bookService, Mockito.times(1)).findBookByIsbn(book.getIsbn());
    }
    @Test
    void whenCheckStockToCartCalledWithNullUserId_itShouldThrowException() {
        String nullUserId = null;

        assertThrows(NullPointerException.class, () -> {
            cartService.checkStock(5,nullUserId);
        });
    }
    @Test
    void whenFindCartbyUserIdCalledWithValidUserId_itShouldReturnCart() {
        Long validUserId = 1L;
        Mockito.when(cartRepository.findByUserId(validUserId)).thenReturn(Optional.of(cart));

        Cart result = cartService.findCartbyUserId(validUserId);

        assertNotNull(result);
        assertEquals(cart, result);
        Mockito.verify(cartRepository).findByUserId(validUserId);
    }

    @Test
    void whenFindCartbyUserIdCalledWithInvalidUserId_itShouldThrowException() {
        Long invalidUserId = 999L;
        Mockito.when(cartRepository.findByUserId(invalidUserId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.findCartbyUserId(invalidUserId);
        });

        assertEquals("Sepet bulunamadı", exception.getMessage());
        Mockito.verify(cartRepository).findByUserId(invalidUserId);
    }

    @Test
    void whenFindCartbyUserIdCalledWithNullUserId_itShouldThrowException() {
        Long nullUserId = null;

        assertThrows(IllegalArgumentException.class, () -> {
            cartService.findCartbyUserId(nullUserId);
        });
    }

}