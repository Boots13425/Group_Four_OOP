package com.university.gradesystem.service;

import com.university.gradesystem.model.User;
import com.university.gradesystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAll() { return repo.findAll(); }

    public Optional<User> getById(Long id) { return repo.findById(id); }

    public User save(User user) {
        if (user.getPassword() != null)
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public void delete(Long id) { repo.deleteById(id); }

    public Optional<User> login(String username, String password) {
        Optional<User> userOpt = repo.findByUsername(username);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public User updateProfile(Long id, String email, String password) {
        User user = repo.findById(id).orElseThrow();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        return repo.save(user);
    }
}