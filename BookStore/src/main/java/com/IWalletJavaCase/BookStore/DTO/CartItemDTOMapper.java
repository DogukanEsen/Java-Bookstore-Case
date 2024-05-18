package com.IWalletJavaCase.BookStore.DTO;

import com.IWalletJavaCase.BookStore.model.CartItem;
import org.springframework.stereotype.Component;

@Component
public class CartItemDTOMapper {
    public CartItemDTO convert(CartItem cartItem){
        return new CartItemDTO(cartItem.getBook().getIsbn(),cartItem.getQuantity());
    }
}
