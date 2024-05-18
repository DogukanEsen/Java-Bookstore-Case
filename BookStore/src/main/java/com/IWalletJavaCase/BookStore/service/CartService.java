package com.IWalletJavaCase.BookStore.service;

import com.IWalletJavaCase.BookStore.DTO.CartItemDTO;
import com.IWalletJavaCase.BookStore.DTO.CartItemDTOMapper;
import com.IWalletJavaCase.BookStore.model.Book;
import com.IWalletJavaCase.BookStore.model.Cart;
import com.IWalletJavaCase.BookStore.model.CartItem;
import com.IWalletJavaCase.BookStore.model.User;
import com.IWalletJavaCase.BookStore.repository.CartItemRepository;
import com.IWalletJavaCase.BookStore.repository.CartRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final BookService bookService;
    private final CartItemDTOMapper cartItemDTOMapper;
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, UserService userService, BookService bookService, CartItemDTOMapper cartItemDTOMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.bookService = bookService;
        this.cartItemDTOMapper = cartItemDTOMapper;
    }


    public List<CartItemDTO> addBookToCart(Long userId, CartItemDTO cartItemDTO) {
        Cart cart = findCartbyUserId(userId);
        //Önceki varlıgın sorgulanması
        CartItem expectedCartItem = findAndUpdateCartItem(cart,cartItemDTO);
        if(expectedCartItem!=null)
            return findCartItems(cart).stream().map(cartItemDTOMapper::convert).collect(Collectors.toList());
        if(checkStock(cartItemDTO.quantity(),cartItemDTO.bookIsbn()) && cartItemDTO.quantity()>=0){
            Book book = bookService.findBookByIsbn(cartItemDTO.bookIsbn());
            CartItem cartItem = new CartItem(book,cartItemDTO.quantity(),cart);
            cartItemRepository.save(cartItem);
            return findCartItems(cart).stream().map(cartItemDTOMapper::convert).collect(Collectors.toList());
        }
        throw new IllegalArgumentException("Stok yetersiz veya negatif değer girildi.");
    }

    public List<CartItemDTO> deleteBookToCart(Long userId, CartItemDTO cartItemDTO){
        if(cartItemDTO.quantity()>0 && bookService.findBookByIsbn(cartItemDTO.bookIsbn())!=null){
            Cart cart = findCartbyUserId(userId);
            CartItemDTO cartItemDTOMinus = new CartItemDTO(cartItemDTO.bookIsbn(),-cartItemDTO.quantity());
            CartItem expectedCartItem = findAndUpdateCartItem(cart,cartItemDTOMinus);

            return findCartItems(cart).stream().map(cartItemDTOMapper::convert).collect(Collectors.toList());
        }
        throw new IllegalArgumentException("Stok yetersiz veya negatif değer girildi.");
    }

    public List<CartItemDTO> getCartItems(Long userId) {
        Cart cart = findCartbyUserId(userId);
        return cartItemRepository.findAllByCart(cart).stream().map(cartItemDTOMapper::convert).collect(Collectors.toList());
    }

    public List<CartItem> findCartItems(Cart cart) {
        return cartItemRepository.findAllByCart(cart);
    }
    public String checkPayment(Long userId){
        Cart cart = findCartbyUserId(userId);
        List<CartItem> cartItems = findCartItems(cart);
        double totalPrice=0;
        for(CartItem item : cartItems){
            totalPrice+=item.getQuantity()*item.getBook().getPrice();
            bookService.removeBookWithIsbn(item.getBook().getIsbn(),item.getQuantity());
            cartItemRepository.delete(item);
        }
        return String.valueOf(totalPrice);
    }
    public CartItem findAndUpdateCartItem(Cart cart, CartItemDTO cartItemDTO){
        List<CartItem> cartItems = findCartItems(cart);
        for(CartItem item : cartItems){
            if(item.getBook().getIsbn().equals(cartItemDTO.bookIsbn())){
                item.setQuantity(item.getQuantity()+cartItemDTO.quantity());
                if(item.getQuantity()==0){
                    cartItemRepository.delete(item);
                    return null;
                }
                if(item.getQuantity()<0){
                    throw new IllegalArgumentException("Sepetteki üründen fazla değer silinemez.");
                }
                if(checkStock(item.getQuantity(), cartItemDTO.bookIsbn()))
                    return cartItemRepository.save(item);
                throw new IllegalArgumentException("Stok Yetersiz");
            }
        }
        return null;
    }
    public boolean checkStock(int quantity, String bookIsbn){
        Book book = bookService.findBookByIsbn(bookIsbn);
        return book.getQuantity()>=quantity;
    }

    public Cart findCartbyUserId(Long userId){
        return cartRepository.findByUserId(userId).orElseThrow(()-> new IllegalArgumentException("Sepet bulunamadı"));
    }
}
