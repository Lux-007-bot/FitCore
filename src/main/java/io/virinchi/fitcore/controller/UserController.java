package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.User;
import io.virinchi.fitcore.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user) {

        userService.registerUser(user);

        return "signup-success";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user, HttpSession session) {

        Optional<User> existingUser =
                userService.findByEmail(user.getEmail());

        if (existingUser.isPresent()
                && userService.checkPassword(
                user.getPassword(),
                existingUser.get().getPassword())) {

            session.setAttribute("loggedInUser", existingUser.get());

            return "redirect:/";
        }

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }
}