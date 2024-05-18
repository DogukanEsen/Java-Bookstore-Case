package com.IWalletJavaCase.BookStore.service;

import com.IWalletJavaCase.BookStore.model.Cart;
import com.IWalletJavaCase.BookStore.model.User;
import com.IWalletJavaCase.BookStore.repository.CartRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateCartService {
private final CartRepository cartRepository;

    public CreateCartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart createCart(User user) {
        if(user==null){
            throw new IllegalArgumentException("User null olamaz.");
        }
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }
}
