package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.Admin;
import io.virinchi.fitcore.model.User;
import io.virinchi.fitcore.service.AdminService;
import io.virinchi.fitcore.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;
    private final AdminService adminService;

    public UserController(
            UserService userService,
            AdminService adminService) {

        this.userService = userService;
        this.adminService = adminService;
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user) {

        userService.registerUser(user);

        return "signup-success";
    }

    @PostMapping("/login")
    public String login(
            @ModelAttribute User user,
            HttpSession session) {

        Optional<Admin> existingAdmin =
                adminService.findByEmail(user.getEmail());

        if (existingAdmin.isPresent()
                && adminService.checkPassword(
                user.getPassword(),
                existingAdmin.get().getPassword())) {

            session.setAttribute(
                    "loggedInAdmin",
                    existingAdmin.get()
            );

            return "redirect:/admin/dashboard";
        }

        Optional<User> existingUser =
                userService.findByEmail(user.getEmail());

        if (existingUser.isPresent()
                && userService.checkPassword(
                user.getPassword(),
                existingUser.get().getPassword())) {

            session.setAttribute(
                    "loggedInUser",
                    existingUser.get()
            );

            return "redirect:/";
        }

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }

    @GetMapping("/admin/users")
    public String manageUsers(
            HttpSession session,
            Model model) {

        Admin admin =
                (Admin) session.getAttribute("loggedInAdmin");

        if (admin == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "users",
                userService.getAllUsers()
        );

        return "admin/users";
    }

    @GetMapping("/admin/users/{id}")
    public String viewUser(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {

        Admin admin =
                (Admin) session.getAttribute("loggedInAdmin");

        if (admin == null) {
            return "redirect:/login";
        }

        Optional<User> user =
                userService.findById(id);

        if (user.isEmpty()) {
            return "redirect:/admin/users";
        }

        model.addAttribute("user", user.get());

        return "admin/user-details";
    }

    @GetMapping("/admin/users/{id}/edit")
    public String editUser(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {

        Admin admin =
                (Admin) session.getAttribute("loggedInAdmin");

        if (admin == null) {
            return "redirect:/login";
        }

        Optional<User> user =
                userService.findById(id);

        if (user.isEmpty()) {
            return "redirect:/admin/users";
        }

        model.addAttribute("user", user.get());

        return "admin/edit-user";
    }

    @PostMapping("/admin/users/{id}/edit")
    public String updateUser(
            @PathVariable Integer id,
            @RequestParam String fullname,
            @RequestParam String email,
            @RequestParam(defaultValue = "false") boolean active,
            @RequestParam(required = false) String password,
            HttpSession session) {

        Admin admin =
                (Admin) session.getAttribute("loggedInAdmin");

        if (admin == null) {
            return "redirect:/login";
        }

        Optional<User> existingUser =
                userService.findById(id);

        if (existingUser.isEmpty()) {
            return "redirect:/admin/users";
        }

        User user = existingUser.get();

        user.setFullname(fullname);
        user.setEmail(email);
        user.setActive(active);

        // Change password only if a new password was entered
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(
                    userService.encodePassword(password)
            );
        }

        userService.updateUser(user);

        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(
            @PathVariable Integer id,
            HttpSession session) {

        Admin admin =
                (Admin) session.getAttribute("loggedInAdmin");

        if (admin == null) {
            return "redirect:/login";
        }

        Optional<User> existingUser =
                userService.findById(id);

        if (existingUser.isEmpty()) {
            return "redirect:/admin/users";
        }

        userService.deleteUser(id);

        return "redirect:/admin/users";
    }

}
