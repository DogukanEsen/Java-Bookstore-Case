package com.IWalletJavaCase.BookStore.DTO;

public record CartItemDTO(
        String bookIsbn,
        int quantity
) {
}
