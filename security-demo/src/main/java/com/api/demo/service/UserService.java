package com.api.demo.service;

import com.api.demo.dto.UserDTO;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<String> signUp(UserDTO userDTO);

    ResponseEntity<String> login(UserDTO userDTO);

}
