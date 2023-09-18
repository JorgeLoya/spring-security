package com.api.demo.service.impl;

import com.api.demo.constants.DemoConstants;
import com.api.demo.dao.UserRepository;
import com.api.demo.pojo.User;
import com.api.demo.security.CustomerDetailsService;
import com.api.demo.security.jwt.JwtFilter;
import com.api.demo.security.jwt.JwtUtil;
import com.api.demo.service.UserService;
import com.api.demo.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Signup for {}", requestMap);

        try {
            if(validateSignUp(requestMap)) {
                User user = userRepository.findByEmail(requestMap.get("email"));
                if(Objects.isNull(user)) {
                    userRepository.save(getUserFromMap(requestMap));
                    return ResponseUtils.getResponseEntity("User successfully added", HttpStatus.CREATED);
                } else {
                    return ResponseUtils.getResponseEntity("The user with that email already exists", HttpStatus.BAD_REQUEST);
                }
            } else {
                return ResponseUtils.getResponseEntity(DemoConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch(Exception exception) {
            exception.printStackTrace();
        }
        return ResponseUtils.getResponseEntity(DemoConstants.INVALID_DATA, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Dentro de login");

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password")));

            if(authentication.isAuthenticated()) {
                return new ResponseEntity<String>("{\"token\": \"" + jwtUtil.generateToken(customerDetailsService.getUserDetail().getEmail(),customerDetailsService.getUserDetail().getRole()) + "\"}",HttpStatus.OK);
            }

        } catch (Exception ex) {
            log.error("{}",ex);
        }
        return new ResponseEntity<String>("{\"message\":\"Incorrect Credentials\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()) {
                Optional<User> optionalUser = userRepository.findById(Integer.parseInt(requestMap.get("id")));
                if(!optionalUser.isEmpty()) {
                    return ResponseUtils.getResponseEntity("Updated user status", HttpStatus.OK);
                } else {
                    ResponseUtils.getResponseEntity(DemoConstants.SOMETHING_WENT_WRONG, HttpStatus.NOT_FOUND);
                }
            } else {
                return ResponseUtils.getResponseEntity(DemoConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtils.getResponseEntity("This user does not exist", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User user = userRepository.findByEmail(jwtFilter.getCurrentUser());

            if(user != null) {
                if(user.getPassword().equals(requestMap.get("oldPassword"))) {
                    user.setPassword(requestMap.get("newPassword"));
                    userRepository.save(user);
                    return ResponseUtils.getResponseEntity("Password updated successfully", HttpStatus.OK);
                }
                return ResponseUtils.getResponseEntity("Incorrect password", HttpStatus.BAD_REQUEST);
            }
            return ResponseUtils.getResponseEntity(DemoConstants.SOMETHING_WENT_WRONG, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtils.getResponseEntity(DemoConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateSignUp(Map<String, String> requestMap) {
        if(requestMap.containsKey("name") && requestMap.containsKey("email") && requestMap.containsKey("password")) {
            return true;
        }
        return false;
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setRole("user");
        return user;
    }

}
