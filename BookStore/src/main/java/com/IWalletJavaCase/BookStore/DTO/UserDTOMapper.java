package com.IWalletJavaCase.BookStore.DTO;

import com.IWalletJavaCase.BookStore.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserDTOMapper {
    public RegisterLoginUserDTO convert(User user){
        return new RegisterLoginUserDTO(user.getUsername(),user.getPassword());
    }
}
