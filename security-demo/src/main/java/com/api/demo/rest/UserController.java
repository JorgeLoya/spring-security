package com.api.demo.rest;

import com.api.demo.constants.DemoConstants;
import com.api.demo.service.UserService;
import com.api.demo.utils.ResponseUtils;
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
    public ResponseEntity<String> registrarUsuario(@RequestBody(required = true) Map<String, String> requestMap) {
        try {
            return userService.signUp(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ResponseUtils.getResponseEntity(DemoConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String, String> requestMap) {
        try {
            return userService.login(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtils.getResponseEntity(DemoConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/greetings")
    public ResponseEntity<String> greetings() {
        return new ResponseEntity<>("Hello Guys!", HttpStatus.OK);
    }

    @GetMapping("/greetings/admin")
    public ResponseEntity<String> adminGreetings() {
        return new ResponseEntity<>("Hello Admin!", HttpStatus.OK);
    }

    @GetMapping("/greetings/user")
    public ResponseEntity<String> userGreetings() {
        return new ResponseEntity<>("Hello User!", HttpStatus.OK);
    }

}
