package com.IWalletJavaCase.BookStore.service;

import com.IWalletJavaCase.BookStore.DTO.RegisterLoginUserDTO;
import com.IWalletJavaCase.BookStore.security.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginUserService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private static final Logger logger = LoggerFactory.getLogger(LoginUserService.class);
    public LoginUserService(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public String loginUser(RegisterLoginUserDTO request){
        if (request.username() == null || request.password() == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        if (authentication.isAuthenticated()) {
            return jwtTokenUtil.generateToken(request.username());
        }
        logger.info("invalid username " + request.username());
        throw new UsernameNotFoundException("invalid username {} " + request.username());
    }
}
