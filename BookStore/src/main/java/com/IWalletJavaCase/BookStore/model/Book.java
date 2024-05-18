package com.IWalletJavaCase.BookStore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "books")
public class Book {
    @Id
    private String isbn;
    @Column(nullable = false)
    private String title;
    private String description;
    @Column(nullable = false)
    private double price;
    @Column(nullable = false)
    private int quantity;

    public Book(String isbn, String title, String description, double price, int quantity) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public Book() {
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn,title,description,price,quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return isbn == book.isbn && Objects.equals(title, book.title)
                && Objects.equals(description, book.description)
                && Objects.equals(price, book.price)
                && Objects.equals(quantity, book.quantity);
    }
}
