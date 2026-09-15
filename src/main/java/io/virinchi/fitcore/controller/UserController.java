package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.Admin;
import io.virinchi.fitcore.model.User;
import io.virinchi.fitcore.service.AdminService;
import io.virinchi.fitcore.service.EmailService;
import io.virinchi.fitcore.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

@Controller
public class UserController {

    private static final Logger logger =
            LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final AdminService adminService;
    private final EmailService emailService;

    public UserController(
            UserService userService,
            AdminService adminService,
            EmailService emailService) {

        this.userService = userService;
        this.adminService = adminService;
        this.emailService = emailService;
    }

    @PostMapping("/signup")
    public String signup(
            @ModelAttribute User user,
            Model model) {

        String fullname = user.getFullname() == null
                ? ""
                : user.getFullname().trim();

        String email = user.getEmail() == null
                ? ""
                : user.getEmail().trim();

        String password = user.getPassword() == null
                ? ""
                : user.getPassword();

        logger.info("User registration attempt: {}", email);

        if (fullname.length() < 2 || fullname.length() > 100) {
            logger.warn("Registration failed - invalid full name: {}", email);

            model.addAttribute(
                    "error",
                    "Full name must be between 2 and 100 characters."
            );
            return "signup";
        }

        if (!Pattern.matches("[A-Za-z ]+", fullname)) {
            logger.warn("Registration failed - invalid name format: {}", email);

            model.addAttribute(
                    "error",
                    "Full name should contain only letters and spaces."
            );
            return "signup";
        }

        if (email.length() > 150
                || !Pattern.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
                email)) {

            logger.warn("Registration failed - invalid email format: {}", email);

            model.addAttribute(
                    "error",
                    "Please enter a valid email address."
            );
            return "signup";
        }

        if (password.length() < 8
                || password.length() > 100) {

            logger.warn("Registration failed - invalid password length: {}", email);

            model.addAttribute(
                    "error",
                    "Password must be between 8 and 100 characters."
            );
            return "signup";
        }

        user.setFullname(fullname);
        user.setEmail(email);

        Optional<User> existingUser =
                userService.findByEmail(email);

        if (existingUser.isPresent()) {
            logger.warn("Registration failed - email already registered: {}", email);

            model.addAttribute(
                    "error",
                    "Email already registered."
            );
            return "signup";
        }

        String verificationCode =
                String.format(
                        "%06d",
                        new Random().nextInt(1000000)
                );

        user.setVerificationCode(verificationCode);
        user.setVerified(false);

        userService.registerUser(user);

        emailService.sendVerificationEmail(
                user.getEmail(),
                user.getFullname(),
                verificationCode
        );

        logger.info("User registered successfully: {}", email);

        model.addAttribute(
                "email",
                user.getEmail()
        );

        return "verify";
    }

    @PostMapping("/verify")
    public String verify(
            @RequestParam String email,
            @RequestParam String verificationCode,
            HttpSession session,
            Model model) {

        Optional<User> existingUser =
                userService.findByEmail(email);

        if (existingUser.isEmpty()) {
            logger.warn("Email verification failed - user not found: {}", email);

            model.addAttribute(
                    "error",
                    "User not found."
            );

            model.addAttribute(
                    "email",
                    email
            );

            return "verify";
        }

        User user = existingUser.get();

        if (user.isVerified()) {
            logger.info("User already verified: {}", email);
            return "redirect:/login";
        }

        if (user.getVerificationCode() != null
                && user.getVerificationCode().equals(verificationCode)) {

            user.setVerified(true);
            user.setVerificationCode(null);

            userService.updateUser(user);

            logger.info("User email verified successfully: {}", email);

            return "redirect:/login?verified=true";
        }

        logger.warn("Invalid email verification attempt: {}", email);

        model.addAttribute(
                "error",
                "Invalid verification code."
        );

        model.addAttribute(
                "email",
                email
        );

        return "verify";
    }

    @PostMapping("/login")
    public String login(
            @ModelAttribute User user,
            HttpSession session,
            Model model) {

        logger.info("Login attempt: {}", user.getEmail());

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

            logger.info("Admin login successful: {}", user.getEmail());

            return "redirect:/admin/dashboard";
        }

        Optional<User> existingUser =
                userService.findByEmail(user.getEmail());

        if (existingUser.isPresent()
                && userService.checkPassword(
                user.getPassword(),
                existingUser.get().getPassword())) {

            if (!existingUser.get().isVerified()) {

                logger.warn(
                        "Login blocked - email not verified: {}",
                        user.getEmail()
                );

                model.addAttribute(
                        "error",
                        "Please verify your email before logging in."
                );

                return "login";
            }

            session.setAttribute(
                    "loggedInUser",
                    existingUser.get()
            );

            logger.info("User login successful: {}", user.getEmail());

            return "redirect:/";
        }

        logger.warn("Invalid login attempt: {}", user.getEmail());

        model.addAttribute(
                "error",
                "Invalid email or password."
        );

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        logger.info("User logout");

        session.invalidate();

        return "redirect:/";
    }

    // =========================================================
    // ADMIN - USER MANAGEMENT
    // SEARCH + PAGINATION
    // =========================================================

    @GetMapping("/admin/users")
    public String manageUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String keyword,
            HttpSession session,
            Model model) {

        Admin admin =
                (Admin) session.getAttribute("loggedInAdmin");

        if (admin == null) {
            logger.warn("Unauthorized attempt to access admin user management");
            return "redirect:/login";
        }

        int pageSize = 10;

        if (page < 0) {
            page = 0;
        }

        keyword = keyword.trim();

        Page<User> userPage;

        if (keyword.isEmpty()) {

            userPage =
                    userService.getUsersPaginated(
                            PageRequest.of(page, pageSize)
                    );

        } else {

            logger.info("Admin searching users with keyword: {}", keyword);

            userPage =
                    userService.searchUsers(
                            keyword,
                            PageRequest.of(page, pageSize)
                    );
        }

        if (userPage.getTotalPages() > 0
                && page >= userPage.getTotalPages()) {

            page = userPage.getTotalPages() - 1;

            if (keyword.isEmpty()) {

                userPage =
                        userService.getUsersPaginated(
                                PageRequest.of(page, pageSize)
                        );

            } else {

                userPage =
                        userService.searchUsers(
                                keyword,
                                PageRequest.of(page, pageSize)
                        );
            }
        }

        int startUser = userPage.getTotalElements() > 0
                ? page * pageSize + 1
                : 0;

        int endUser = Math.min(
                (page + 1) * pageSize,
                (int) userPage.getTotalElements()
        );

        model.addAttribute(
                "users",
                userPage.getContent()
        );

        model.addAttribute(
                "currentPage",
                page
        );

        model.addAttribute(
                "totalPages",
                userPage.getTotalPages()
        );

        model.addAttribute(
                "totalUsers",
                userPage.getTotalElements()
        );

        model.addAttribute(
                "pageSize",
                pageSize
        );

        model.addAttribute(
                "startUser",
                startUser
        );

        model.addAttribute(
                "endUser",
                endUser
        );

        model.addAttribute(
                "keyword",
                keyword
        );

        return "admin/users";
    }

    @GetMapping("/admin/users/search")
    @ResponseBody
    public Page<User> searchUsersAjax(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            HttpSession session) {

        Admin admin =
                (Admin) session.getAttribute("loggedInAdmin");

        if (admin == null) {
            throw new RuntimeException("Unauthorized");
        }

        int pageSize = 10;

        keyword = keyword.trim();

        if (keyword.isEmpty()) {
            return userService.getUsersPaginated(
                    PageRequest.of(page, pageSize)
            );
        }

        return userService.searchUsers(
                keyword,
                PageRequest.of(page, pageSize)
        );
    }

    @GetMapping("/admin/users/{id}")
    public String viewUser(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {

        Admin admin =
                (Admin) session.getAttribute("loggedInAdmin");

        if (admin == null) {
            logger.warn(
                    "Unauthorized attempt to view user: userId={}",
                    id
            );
            return "redirect:/login";
        }

        Optional<User> user =
                userService.findById(id);

        if (user.isEmpty()) {
            logger.warn(
                    "Admin attempted to view non-existent user: userId={}",
                    id
            );
            return "redirect:/admin/users";
        }

        model.addAttribute(
                "user",
                user.get()
        );

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
            logger.warn(
                    "Unauthorized attempt to edit user: userId={}",
                    id
            );
            return "redirect:/login";
        }

        Optional<User> user =
                userService.findById(id);

        if (user.isEmpty()) {
            logger.warn(
                    "Admin attempted to edit non-existent user: userId={}",
                    id
            );
            return "redirect:/admin/users";
        }

        model.addAttribute(
                "user",
                user.get()
        );

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
            logger.warn(
                    "Unauthorized attempt to update user: userId={}",
                    id
            );
            return "redirect:/login";
        }

        Optional<User> existingUser =
                userService.findById(id);

        if (existingUser.isEmpty()) {
            logger.warn(
                    "Admin attempted to update non-existent user: userId={}",
                    id
            );
            return "redirect:/admin/users";
        }

        User user = existingUser.get();

        user.setFullname(fullname);
        user.setEmail(email);
        user.setActive(active);

        if (password != null
                && !password.trim().isEmpty()) {

            user.setPassword(
                    userService.encodePassword(password)
            );

            logger.info(
                    "Admin updated user details and password: userId={}",
                    id
            );

        } else {

            logger.info(
                    "Admin updated user details: userId={}",
                    id
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
            logger.warn(
                    "Unauthorized attempt to delete user: userId={}",
                    id
            );
            return "redirect:/login";
        }

        Optional<User> existingUser =
                userService.findById(id);

        if (existingUser.isEmpty()) {
            logger.warn(
                    "Admin attempted to delete non-existent user: userId={}",
                    id
            );
            return "redirect:/admin/users";
        }

        userService.deleteUser(id);

        logger.info(
                "Admin deleted user: userId={}",
                id
        );

        return "redirect:/admin/users";
    }
}