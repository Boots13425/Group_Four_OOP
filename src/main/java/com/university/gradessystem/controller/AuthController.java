package com.university.gradessystem.controller;

import com.university.gradessystem.model.User;
import com.university.gradessystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        if (user.getRole() == User.Role.ROLE_ADMIN) {
            return "redirect:/admin";
        } else if (user.getRole() == User.Role.ROLE_PROFESSOR) {
            return "redirect:/professor";
        } else if (user.getRole() == User.Role.ROLE_STUDENT) {
            return "redirect:/student";
        }
        
        return "redirect:/login";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return "redirect:/login?logout";
    }
}
