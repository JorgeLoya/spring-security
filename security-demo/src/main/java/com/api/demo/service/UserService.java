package com.api.demo.service;

import com.api.demo.dto.UserDTO;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserService {

    ResponseEntity<String> signUp(UserDTO userDTO);

    ResponseEntity<String> login(UserDTO userDTO);

    ResponseEntity<String> update(Map<String, String> requestMap);

    ResponseEntity<String> changePassword(Map<String, String> requestMap);

}
