package com.api.demo.rest;

import com.api.demo.constants.DemoConstants;
import com.api.demo.dto.UserDTO;
import com.api.demo.service.UserService;
import com.api.demo.utils.ResponseUtils;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> registrarUsuario(@RequestBody(required = true) UserDTO userDTO) {
        try {
            return userService.signUp(userDTO);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ResponseUtils.getResponseEntity(DemoConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody(required = true) UserDTO userDTO) {
        try {
            return userService.login(userDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtils.getResponseEntity(DemoConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/greetings")
    public ResponseEntity<String> greetings() {
        return new ResponseEntity<>("Hello Guys!", HttpStatus.OK);
    }

    @GetMapping("/greetings/editor")
    @RolesAllowed("ROLE_EDITOR")
    public ResponseEntity<String> editorGreetings() {
        return new ResponseEntity<>("Hello Editor!", HttpStatus.OK);
    }

    @GetMapping("/greetings/customer")
    @RolesAllowed("ROLE_CUSTOMER")
    public ResponseEntity<String> customerGreetings() {
        return new ResponseEntity<>("Hello Customer!", HttpStatus.OK);
    }

    @GetMapping("/greetings/custedit")
    @RolesAllowed({"ROLE_CUSTOMER", "ROLE_EDITOR"})
    public ResponseEntity<String> custeditGreetings() {
        return new ResponseEntity<>("Hello Customer or Editor!", HttpStatus.OK);
    }

}
