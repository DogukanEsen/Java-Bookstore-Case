package com.IWalletJavaCase.BookStore.controller;

import com.IWalletJavaCase.BookStore.DTO.CartItemDTO;
import com.IWalletJavaCase.BookStore.service.CartService;
import com.IWalletJavaCase.BookStore.service.GetDetailsFromRequestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final GetDetailsFromRequestService getDetailsFromRequestService;

    public CartController(CartService cartService, GetDetailsFromRequestService getDetailsFromRequestService) {
        this.cartService = cartService;
        this.getDetailsFromRequestService = getDetailsFromRequestService;
    }

    @PostMapping("/addItem")
    public ResponseEntity<List<CartItemDTO>> addBookToCart(HttpServletRequest request, @RequestBody CartItemDTO cartItemDTO) {
        Long userId = getDetailsFromRequestService.getUserIdFromJwt(request);
        return ResponseEntity.ok(cartService.addBookToCart(userId, cartItemDTO));
    }

    @PostMapping("/deleteItem")
    public ResponseEntity<List<CartItemDTO>> deleteBookToCart(HttpServletRequest request, @RequestBody CartItemDTO cartItemDTO) {
        Long userId = getDetailsFromRequestService.getUserIdFromJwt(request);
        return ResponseEntity.ok(cartService.deleteBookToCart(userId, cartItemDTO));
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItemDTO>> getCartItems(HttpServletRequest request) {
        Long userId = getDetailsFromRequestService.getUserIdFromJwt(request);
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }
    @PostMapping("/payment")
    public ResponseEntity<String> checkPayment(HttpServletRequest request){
        Long userId = getDetailsFromRequestService.getUserIdFromJwt(request);
        String totalPrice = cartService.checkPayment(userId);
        return ResponseEntity.ok("Toplam ücret " + totalPrice + " olarak hesaplanmıştır.\n" +
                " sepet başarıyla onaylandı!!");
    }
}
