package com.IWalletJavaCase.BookStore.service;

import com.IWalletJavaCase.BookStore.DTO.BookDTOMapper;
import com.IWalletJavaCase.BookStore.DTO.SaveBookDTO;
import com.IWalletJavaCase.BookStore.exception.BookNotFoundException;
import com.IWalletJavaCase.BookStore.model.Book;
import com.IWalletJavaCase.BookStore.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookServiceTest {
    private BookService bookService;
    private BookRepository bookRepository;
    private BookDTOMapper bookDTOMapper;
    @BeforeEach
    public void setUp() throws Exception{
        bookRepository = Mockito.mock(BookRepository.class);
        bookDTOMapper = Mockito.mock(BookDTOMapper.class);

        bookService = new BookService(bookRepository,bookDTOMapper);
    }

    @Test
    public void whenSaveBookCalledWithValidRequest_itShouldReturnValidSaveBookDTO(){
        SaveBookDTO saveBookRequest = new SaveBookDTO("1233","title","description",5.0,5);
        Book book = new Book("1233", "title", "description", 5.0, 5);

        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(bookDTOMapper.convert(book)).thenReturn(saveBookRequest);

        SaveBookDTO result = bookService.saveBook(saveBookRequest);

        assertEquals(result,saveBookRequest);

        Mockito.verify(bookRepository).save(book);
        Mockito.verify(bookDTOMapper).convert(book);
    }
    @Test
    public void WhenGetAllBooksCalledWithValidRequest_itShouldReturnValidListSaveBookDTO(){
        SaveBookDTO saveBookRequest = new SaveBookDTO("1233","title","description",5.0,5);
        Book book = new Book("1233", "title", "description", 5.0, 5);

        List<Book> books = Arrays.asList(book);

        Mockito.when(bookRepository.findAll()).thenReturn(books);
        Mockito.when(bookDTOMapper.convert(book)).thenReturn(saveBookRequest);

        List<SaveBookDTO> result = bookService.getAllBooks();

        assertEquals(1, result.size());
        assertEquals(saveBookRequest, result.get(0));
    }
    @Test
    public void WhenFindBookByIsbnCalledWithValidIsbn_itShouldReturnValidBook(){
        Book book = new Book("1233", "title", "description", 5.0, 5);

        Mockito.when(bookRepository.findByIsbn("1233")).thenReturn(Optional.of(book));

        Book result = bookService.findBookByIsbn("1233");

        assertEquals(book, result);
    }
    @Test
    public void WhenFindBookByIsbnCalledWithInvalidIsbn_itShouldReturnBookNotFoundException(){
        Mockito.when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.findBookByIsbn("1234567890"));
    }
}