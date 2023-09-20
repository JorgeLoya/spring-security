package com.api.demo.service.impl;

import com.api.demo.constants.DemoConstants;
import com.api.demo.dao.UserRepository;
import com.api.demo.dto.UserDTO;
import com.api.demo.pojo.User;
import com.api.demo.security.CustomerDetailsService;
import com.api.demo.security.jwt.JwtFilter;
import com.api.demo.security.jwt.JwtUtil;
import com.api.demo.service.UserService;
import com.api.demo.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
    public ResponseEntity<String> signUp(UserDTO userDTO) {
        log.info("Signup for {}", userDTO);

        try {
            if(validateSignUp(userDTO)) {
                User user = userRepository.findByEmail(userDTO.getEmail());
                if(Objects.isNull(user)) {
                    userRepository.save(getUserFromMap(userDTO));
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
    public ResponseEntity<String> login(UserDTO userDTO) {
        log.info("Dentro de login");

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword()));

            if(authentication.isAuthenticated()) {
                return new ResponseEntity<String>("{\"token\": \"" + jwtUtil.createToken(customerDetailsService.getUserDetail().getEmail(),customerDetailsService.getUserDetail().getRoles()) + "\"}",HttpStatus.OK);
            }

        } catch (Exception ex) {
            log.error("{}",ex);
        }
        return new ResponseEntity<String>("{\"message\":\"Incorrect Credentials\"}", HttpStatus.BAD_REQUEST);
    }

    private boolean validateSignUp(UserDTO userDTO) {
        if(!Strings.isEmpty(userDTO.getName()) && !Strings.isEmpty(userDTO.getEmail()) && !Strings.isEmpty(userDTO.getPassword())) {
            return true;
        }
        return false;
    }

    private User getUserFromMap(UserDTO userDTO) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode(userDTO.getPassword());

        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(password);
        user.setRoles(null);
        return user;
    }

}
