package com.hardware_today.controller;

import com.hardware_today.model.OutputModel;
import com.hardware_today.model.UserModel;
import com.hardware_today.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String validateUser(@RequestBody UserModel user) {
        try {
            return userService.validateUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping
    public UserModel createUser(@RequestBody UserModel user) {
        try {
            return userService.saveUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
