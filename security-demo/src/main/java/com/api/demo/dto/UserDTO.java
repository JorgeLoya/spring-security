package com.api.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {

    private Integer id;

    private String email;

    private String password;

    private String name;

    private List<String> roles;

    private String newPassword;

    private String oldPassword;

}
