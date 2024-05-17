package com.IWalletJavaCase.BookStore.DTO;

public record SaveBookDTO(
    String isbn,
    String title,
    String description,
    double price,
    int quantity
) {
}
