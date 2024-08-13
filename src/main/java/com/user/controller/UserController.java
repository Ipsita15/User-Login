package com.user.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.model.UserModel;
import com.user.services.LoginService;
import com.user.services.UserService;



@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private LoginService loginService;

    
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody UserModel user) {
        // Call the service to create the user and return the response
        return userService.createUser(user);
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

       	ResponseEntity<Map<String,Object>> map = loginService.loginUser(email, password);
       			
        return map; 
    }
}
