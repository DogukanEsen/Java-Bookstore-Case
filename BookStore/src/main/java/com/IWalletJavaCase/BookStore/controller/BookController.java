package com.IWalletJavaCase.BookStore.controller;

import com.IWalletJavaCase.BookStore.DTO.SaveBookDTO;
import com.IWalletJavaCase.BookStore.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<SaveBookDTO>> getAllBooks(){
        return ResponseEntity.ok(bookService.getAllBooks());
    }
}
