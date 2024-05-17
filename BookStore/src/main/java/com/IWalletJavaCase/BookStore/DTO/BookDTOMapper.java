package com.IWalletJavaCase.BookStore.DTO;

import com.IWalletJavaCase.BookStore.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookDTOMapper {
    public SaveBookDTO convert(Book book){
        return new SaveBookDTO(book.getIsbn(),book.getTitle(),book.getDescription(),book.getPrice(),book.getQuantity());
    }
}
