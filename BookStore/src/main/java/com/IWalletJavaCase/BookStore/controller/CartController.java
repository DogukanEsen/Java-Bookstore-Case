package com.IWalletJavaCase.BookStore.controller;

import com.IWalletJavaCase.BookStore.DTO.CartItemDTO;
import com.IWalletJavaCase.BookStore.model.CartItem;
import com.IWalletJavaCase.BookStore.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;


    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/addItem")
    public ResponseEntity<List<CartItem>> addBookToCart(HttpServletRequest request, @RequestBody CartItemDTO cartItemDTO) {

        return ResponseEntity.ok(cartService.addBookToCart(request, cartItemDTO));
    }

    @PostMapping("/deleteItem")
    public ResponseEntity<List<CartItem>> deleteBookToCart(HttpServletRequest request, @RequestBody CartItemDTO cartItemDTO) {

        return ResponseEntity.ok(cartService.deleteBookToCart(request, cartItemDTO));
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItem>> getCartItems(HttpServletRequest request) {
        return ResponseEntity.ok(cartService.getCartItems(request));
    }
    @PostMapping("/payment")
    public ResponseEntity<String> checkPayment(HttpServletRequest request){
        String totalPrice = cartService.checkPayment(request);
        return ResponseEntity.ok("Toplam ücret " + totalPrice + " olarak hesaplanmıştır.\n" +
                                        " sepet başarıyla onaylandı!!");
    }
}
