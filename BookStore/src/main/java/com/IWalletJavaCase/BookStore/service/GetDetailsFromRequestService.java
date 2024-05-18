package com.IWalletJavaCase.BookStore.service;

import com.IWalletJavaCase.BookStore.security.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetDetailsFromRequestService {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    public GetDetailsFromRequestService(JwtTokenUtil jwtTokenUtil, UserService userService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    public String getUsernameFromJwt(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            username = jwtTokenUtil.extractUsername(token);
        }
        if(username == null)
            throw new UsernameNotFoundException("Kullanici Bulunamadi");
        return username;
    }

    public Long getUserIdFromJwt(HttpServletRequest request){
        String username = getUsernameFromJwt(request);
        return userService.findUserIdbyUsername(username);
    }
}
