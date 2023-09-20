package com.api.demo.service.impl;

import com.api.demo.constants.DemoConstants;
import com.api.demo.dao.UserRepository;
import com.api.demo.dto.UserDTO;
import com.api.demo.mapper.UserMapper;
import com.api.demo.pojo.User;
import com.api.demo.security.CustomerDetailsService;
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

    @Override
    public ResponseEntity<String> signUp(UserDTO userDTO) {
        log.info("Signup for {}", userDTO);

        if(validateSignUp(userDTO)) {
            User user = userRepository.findByEmail(userDTO.getEmail());
            if(Objects.isNull(user)) {
                user = UserMapper.dtoToUser(userDTO);
                user.setPassword(encodePassword(userDTO.getPassword()));
                userRepository.save(user);
                return ResponseUtils.getResponseEntity("User successfully added", HttpStatus.CREATED);
            } else {
                return ResponseUtils.getResponseEntity("The user with that email already exists", HttpStatus.BAD_REQUEST);
            }
        } else {
            return ResponseUtils.getResponseEntity(DemoConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> login(UserDTO userDTO) {
        log.info("Inside login");

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword()));

        if(authentication.isAuthenticated()) {
            return new ResponseEntity<String>("{\"token\": \"" + jwtUtil.createToken(customerDetailsService.getUserDetail().getEmail(),customerDetailsService.getUserDetail().getRoles()) + "\"}",HttpStatus.OK);
        }

        return new ResponseEntity<String>("{\"message\":\"Incorrect Credentials\"}", HttpStatus.BAD_REQUEST);
    }

    private boolean validateSignUp(UserDTO userDTO) {
        if(!Strings.isEmpty(userDTO.getName()) && !Strings.isEmpty(userDTO.getEmail()) && !Strings.isEmpty(userDTO.getPassword())) {
            return true;
        }
        return false;
    }

    private String encodePassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

}
