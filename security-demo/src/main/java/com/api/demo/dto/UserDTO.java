package com.api.demo.dto;

import com.api.demo.pojo.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {

    private Integer id;

    private String email;

    private String password;

    private String name;

    private Set<Role> roles;

    private String newPassword;

    private String oldPassword;

}
