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
    private final CreateCartService createCartService;
    private final TokenService tokenService;
    private final UserDTOMapper userDTOMapper;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, CreateCartService createCartService, TokenService tokenService, UserDTOMapper userDTOMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.createCartService = createCartService;
        this.tokenService = tokenService;
        this.userDTOMapper = userDTOMapper;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElseThrow(()->new UsernameNotFoundException("Username kayıtlı degil."));
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("Username kayıtlı degil."));
    }

    public RegisterLoginUserDTO registerUser(RegisterLoginUserDTO userRequest){
        if(userRepository.findByUsername(userRequest.username()).isPresent())
            throw new UsernameAlreadyExistsException("Username " + userRequest.username() + " zaten var.");
        User user = userRepository.save(new User(userRequest.username(),passwordEncoder.encode(userRequest.password())));
        createCartService.createCart(user);
        return userDTOMapper.convert(user);
    }


    public User findUserbyUserId(Long userid){
        return userRepository.findById(userid).orElseThrow(()->new IllegalArgumentException("Username kayıtlı degil."));
    }
    public String logout(String authorizationHeader){
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            tokenService.invalidateToken(token);
        }
        return "Logged out successfully.";
    }

    public Long findUserIdbyUsername(String username){
        User user =  userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("Username kayıtlı degil."));
        return user.getId();
    }
}

