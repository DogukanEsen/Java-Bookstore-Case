package com.IWalletJavaCase.BookStore.repository;

import com.IWalletJavaCase.BookStore.model.Cart;
import com.IWalletJavaCase.BookStore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findAllByCart(Cart cart);
}
