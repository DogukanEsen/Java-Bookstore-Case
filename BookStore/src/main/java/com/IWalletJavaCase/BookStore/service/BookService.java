package com.IWalletJavaCase.BookStore.service;

import com.IWalletJavaCase.BookStore.DTO.BookDTOMapper;
import com.IWalletJavaCase.BookStore.DTO.SaveBookDTO;
import com.IWalletJavaCase.BookStore.exception.BookNotFoundException;
import com.IWalletJavaCase.BookStore.model.Book;
import com.IWalletJavaCase.BookStore.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookDTOMapper bookDTOMapper;

    public BookService(BookRepository bookRepository, BookDTOMapper bookDTOMapper) {
        this.bookRepository = bookRepository;
        this.bookDTOMapper = bookDTOMapper;
    }

    public SaveBookDTO saveBook (SaveBookDTO bookRequest){
        Book book = new Book(bookRequest.isbn(), bookRequest.title(), bookRequest.description(), bookRequest.price(), bookRequest.quantity());
        return bookDTOMapper.convert(bookRepository.save(book));
    }

    public List<SaveBookDTO> getAllBooks(){
        return bookRepository.findAll().stream().map(bookDTOMapper::convert).collect(Collectors.toList());
    }

    public Book findBookByIsbn(String isbn){
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() ->new BookNotFoundException("Kitap Bulunamadı"));
    }
    public void removeBookWithIsbn(String isbn, int quantity){
        Book book = findBookByIsbn(isbn);
        book.setQuantity(book.getQuantity()-quantity);
        bookRepository.save(book);
    }
}
