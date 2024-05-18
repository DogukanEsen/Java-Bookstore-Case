package com.IWalletJavaCase.BookStore.service;

import com.IWalletJavaCase.BookStore.DTO.CartItemDTO;
import com.IWalletJavaCase.BookStore.model.Book;
import com.IWalletJavaCase.BookStore.model.Cart;
import com.IWalletJavaCase.BookStore.model.CartItem;
import com.IWalletJavaCase.BookStore.model.User;
import com.IWalletJavaCase.BookStore.repository.CartItemRepository;
import com.IWalletJavaCase.BookStore.repository.CartRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final BookService bookService;
    private final CreateCartService createCartService;
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, UserService userService, BookService bookService, CreateCartService createCartService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.bookService = bookService;
        this.createCartService = createCartService;
    }

    public Cart createCart(User user) {
        return createCartService.createCart(user);
    }
    public Cart getCartByJwt(HttpServletRequest request){
        String username=userService.getUsernameFromRequest(request);
        User user = userService.getByUsername(username);
        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
        return cart;
    }
    public List<CartItem> addBookToCart(HttpServletRequest request, CartItemDTO cartItemDTO) {
        Cart cart = getCartByJwt(request);
        //Önceki varlıgın sorgulanması
        CartItem expectedCartItem = findAndUpdateCartItem(cart,cartItemDTO);
        if(expectedCartItem!=null)
            return findCartItems(cart);
        if(checkStock(cartItemDTO.quantity(),cartItemDTO.bookIsbn()) && cartItemDTO.quantity()>0){
            Book book = bookService.findBookByIsbn(cartItemDTO.bookIsbn());
            CartItem cartItem = new CartItem(book,cartItemDTO.quantity(),cart);
            cartItemRepository.save(cartItem);
            return findCartItems(cart);
        }
        throw new IllegalArgumentException("Stok yetersiz veya negatif değer girildi.");
    }

    public List<CartItem> deleteBookToCart(HttpServletRequest request, CartItemDTO cartItemDTO){
        Cart cart = getCartByJwt(request);
        CartItemDTO cartItemDTOMinus = new CartItemDTO(cartItemDTO.bookIsbn(),-cartItemDTO.quantity());
        CartItem expectedCartItem = findAndUpdateCartItem(cart,cartItemDTOMinus);

        return findCartItems(cart);
    }

    public List<CartItem> getCartItems(HttpServletRequest request) {
        Cart cart = getCartByJwt(request);
        return cartItemRepository.findAllByCart(cart);
    }

    public List<CartItem> findCartItems(Cart cart) {
        return cartItemRepository.findAllByCart(cart);
    }
    public String checkPayment(HttpServletRequest request){
        Cart cart = getCartByJwt(request);
        List<CartItem> cartItems = findCartItems(cart);
        double totalPrice=0;
        for(CartItem item : cartItems){
            totalPrice+=item.getQuantity()*item.getBook().getPrice();
            cartItemRepository.delete(item);
        }
        return String.valueOf(totalPrice);
    }
    public CartItem findAndUpdateCartItem(Cart cart, CartItemDTO cartItemDTO){
        List<CartItem> cartItems = findCartItems(cart);
        for(CartItem item : cartItems){
            if(item.getBook().getIsbn().equals(cartItemDTO.bookIsbn())){
                item.setQuantity(item.getQuantity()+cartItemDTO.quantity());
                if(item.getQuantity()==0){
                    cartItemRepository.delete(item);
                    return null;
                }
                if(item.getQuantity()<0){
                    throw new IllegalArgumentException("Sepetteki üründen fazla değer silinemez.");
                }
                if(checkStock(item.getQuantity(), cartItemDTO.bookIsbn()))
                    return cartItemRepository.save(item);
                throw new IllegalArgumentException("Stok Yetersiz");
            }
        }
            return null;
    }
    public boolean checkStock(int quantity, String bookIsbn){
        Book book = bookService.findBookByIsbn(bookIsbn);
        return book.getQuantity()>=quantity;
    }
}
