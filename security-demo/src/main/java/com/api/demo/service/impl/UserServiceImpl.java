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
        user.setRole("user");
        return user;
    }

}
