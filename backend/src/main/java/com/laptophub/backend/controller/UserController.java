package com.laptophub.backend.controller;

import com.laptophub.backend.model.User;
import com.laptophub.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @GetMapping
    public Page<User> findAll(Pageable pageable) {
        return userService.findAll(pageable);
    }

    @GetMapping("/email")
    public User findByEmail(@RequestParam String email) {
        return userService.findByEmail(email);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable UUID id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }
}