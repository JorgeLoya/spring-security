package com.api.demo.service;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {

    ResponseEntity<String> signUp(Map<String, String> requestMap);

    ResponseEntity<String> login(Map<String, String> requestMap);

    ResponseEntity<String> update(Map<String, String> requestMap);

    ResponseEntity<String> changePassword(Map<String, String> requestMap);

}
