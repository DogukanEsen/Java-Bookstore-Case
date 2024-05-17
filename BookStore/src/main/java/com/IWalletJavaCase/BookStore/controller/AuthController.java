package com.IWalletJavaCase.BookStore.controller;

import com.IWalletJavaCase.BookStore.DTO.RegisterLoginUserDTO;
import com.IWalletJavaCase.BookStore.model.User;
import com.IWalletJavaCase.BookStore.security.JwtTokenUtil;
import com.IWalletJavaCase.BookStore.service.TokenService;
import com.IWalletJavaCase.BookStore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final LoginUserService loginUserService;
    public AuthController(UserService userService, LoginUserService loginUserService) {
        this.userService = userService;
        this.loginUserService = loginUserService;
    }

    @PostMapping("/registerUser")
    public ResponseEntity<User> addUser(@RequestBody RegisterLoginUserDTO request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @PostMapping("/loginUser")
    public ResponseEntity<String> generateToken(@RequestBody RegisterLoginUserDTO request) {
        return ResponseEntity.ok(loginUserService.loginUser(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok(userService.logout(authorizationHeader));
    }
}
