package com.example.shop.controller;

import com.example.shop.model.User;
import com.example.shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    // Show login form
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    
    // Process login form
    @PostMapping("/login")
    public String processLogin(@RequestParam String email, 
                              @RequestParam String password, 
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            // Login successful, store user in session
            User user = userOpt.get();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userName", user.getFirstName() + " " + user.getLastName());
            session.setAttribute("isLoggedIn", true);
            
            return "redirect:/";
        } else {
            // Login failed
            redirectAttributes.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/login";
        }
    }
    
    // Process registration form
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, 
                               @RequestParam(required = false) String birthDate,
                               @RequestParam(required = false, defaultValue = "false") Boolean newsletter,
                               RedirectAttributes redirectAttributes) {
        // Check if user already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Email already registered");
            return "redirect:/#register";
        }
        
        // Set newsletter subscription
        user.setNewsletterSubscription(newsletter);
        
        // Parse birth date if provided
        if (birthDate != null && !birthDate.isEmpty()) {
            try {
                user.setBirthDate(LocalDate.parse(birthDate));
            } catch (Exception e) {
                // Ignore date parsing errors
            }
        }
        
        // Save the new user
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
        return "redirect:/login";
    }
    
    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}