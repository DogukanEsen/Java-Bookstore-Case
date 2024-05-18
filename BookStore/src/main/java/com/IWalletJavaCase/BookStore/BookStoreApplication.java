package com.IWalletJavaCase.BookStore;

import com.IWalletJavaCase.BookStore.DTO.SaveBookDTO;
import com.IWalletJavaCase.BookStore.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookStoreApplication.class, args);
	}


	@Bean
	CommandLineRunner run(BookService bookService){
		return args -> {
			if(bookService.getAllBooks().isEmpty()){
				bookService.saveBook(new SaveBookDTO("1","deneme1_title","deneme1_description",1.5,5));
				bookService.saveBook(new SaveBookDTO("2","deneme2_title","deneme2_description",2.5,5));
				bookService.saveBook(new SaveBookDTO("3","deneme3_title","deneme3_description",3.5,5));
				bookService.saveBook(new SaveBookDTO("4","deneme4_title","deneme4_description",4.5,5));
			}
		};
	}
}
