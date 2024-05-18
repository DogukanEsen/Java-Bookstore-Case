package com.IWalletJavaCase.BookStore.service;

import com.IWalletJavaCase.BookStore.DTO.RegisterLoginUserDTO;
import com.IWalletJavaCase.BookStore.DTO.UserDTOMapper;
import com.IWalletJavaCase.BookStore.exception.UsernameAlreadyExistsException;
import com.IWalletJavaCase.BookStore.model.User;
import com.IWalletJavaCase.BookStore.repository.UserRepository;
import com.IWalletJavaCase.BookStore.security.JwtTokenUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class UserService implements UserDetailsService  {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final CreateCartService createCartService;
    private final TokenService tokenService;
    private final UserDTOMapper userDTOMapper;
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil, CreateCartService createCartService, TokenService tokenService, UserDTOMapper userDTOMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.createCartService = createCartService;
        this.tokenService = tokenService;
        this.userDTOMapper = userDTOMapper;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElseThrow(EntityNotFoundException::new);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public RegisterLoginUserDTO registerUser(RegisterLoginUserDTO userRequest){
        if(userRepository.findByUsername(userRequest.username()).isPresent())
            throw new UsernameAlreadyExistsException("Username " + userRequest.username() + " zaten var.");
        User user = userRepository.save(new User(userRequest.username(),passwordEncoder.encode(userRequest.password())));
        createCartService.createCart(user);
        return userDTOMapper.convert(user);
    }

    public String getUsernameFromRequest(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            username = jwtTokenUtil.extractUsername(token);
        }
        return username;
    }

    public User findUserbyUserId(Long userid){
        return userRepository.findById(userid).orElse(null);
    }
    public String logout(String authorizationHeader){
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            tokenService.invalidateToken(token);
        }
        return "Logged out successfully.";
    }
}

