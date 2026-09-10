package io.virinchi.fitcore.controller;


import io.virinchi.fitcore.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user != null) {
            model.addAttribute("loggedInUser", user);
        }

        return "homepage";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/blog")
    public String blog() {
        return "blog";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/gallery")
    public String gallery() {
        return "gallery";
    }

    @GetMapping("/location")
    public String location() {
        return "location";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }



    @GetMapping("/ram")
    public String ram() {
        return "ram";
    }

    @GetMapping("/sita")
    public String sita() {
        return "sita";
    }

    @GetMapping("/laxman")
    public String laxman() {
        return "laxman";
    }
}