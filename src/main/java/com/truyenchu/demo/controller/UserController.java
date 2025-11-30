package com.truyenchu.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;
    
        @GetMapping("/user/{userId}")
        public ResponseEntity<User> getUser(@PathVariable Long userId) {
            User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }
    
}
