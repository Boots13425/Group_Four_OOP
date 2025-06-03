package com.university.gradesystem.controller;

import com.university.gradesystem.model.User;
import com.university.gradesystem.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping
    public List<User> getAll() { return userService.getAll(); }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userService.getById(id).orElse(null);
    }

    @PostMapping
    public User create(@RequestBody User user) { return userService.save(user); }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) {
        user.setUserID(id);
        return userService.save(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { userService.delete(id); }

    // Login
    @PostMapping("/login")
    public User login(@RequestParam String username, @RequestParam String password) {
        return userService.login(username, password).orElse(null);
    }

    // Update profile
    @PutMapping("/{id}/profile")
    public User updateProfile(@PathVariable Long id, @RequestParam String email, @RequestParam String password) {
        return userService.updateProfile(id, email, password);
    }
}
