package com.api.demo.mapper;

import com.api.demo.dto.UserDTO;
import com.api.demo.pojo.User;

public class UserMapper {

    private UserMapper() {

    }

    public static User dtoToUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(user.getEmail());
        user.setRoles(userDTO.getRoles());
        return user;
    }

}
