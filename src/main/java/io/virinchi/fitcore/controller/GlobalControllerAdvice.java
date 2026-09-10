package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("loggedInUser")
    public User loggedInUser(HttpSession session) {

        return (User) session.getAttribute("loggedInUser");
    }
}