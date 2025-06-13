package com.example.project.controller;

import com.example.project.model.UserPrincipal;
import com.example.project.model.Users;
import com.example.project.service.AdminService;
import com.example.project.service.AuthenticationService;
import com.example.project.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final AdminService adminService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(final Model model) {
        model.addAttribute("user", new Users());
        return "signup";
    }

    @PostMapping("/send-email-reset-password")
    public String resetPassword(final Model model, @ModelAttribute("email") final String email) {
        authenticationService.sendMailToResetPassword(email);
        return "linkResetSendToEmail";
    }

    @GetMapping("/reset-password")
    public String toResetPasswordPage() {
        return "resetPassword";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam final String token,
            @RequestParam final String newPassword,
            final Model model
    ) {
        final boolean success = authenticationService.changePassWord(newPassword, token);
        if (success) {
            model.addAttribute("message", "success!");
        } else {
            model.addAttribute("message", "failed");
        }

        return "ResetPasswordAfter";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgotPassword";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") final Users user, final Model model) {
        final boolean registrationResult = authenticationService.registerNewUserAccount(user);
        if (registrationResult) {
            return "redirect:/login?registrationSuccess";
        } else {
            model.addAttribute("registrationError", true);
            return "signup";
        }
    }

    @GetMapping("/adminPage")
    public String adminPage(final Model model) {
        final var dashboard = adminService.getDashboard();
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("contentTemplate", "admins/dashboard");

        return "admins/layout";
    }

    @PostMapping("/change_password")
    public String changePassword(final Model model, @ModelAttribute("changePasswordDto") final Users updateUser) {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users user = new Users();

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                final String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                user = authenticationService.getInforUserByGmail(gmail);

                if (user != null) {
                    model.addAttribute("user_account", user);
                }
            } else if (principal instanceof UserPrincipal) {
                final UserPrincipal userPrincipal = (UserPrincipal) principal;
                final String username = userPrincipal.getUsername();

                user = authenticationService.getInforUser(username);

                if (user != null) {
                    model.addAttribute("user_account", user);
                }
            }
        }

        final Users existingUser = userService.getUserById(updateUser.getId());
        final boolean checkChange = authenticationService.changeUserPassword(existingUser, updateUser.getPassword());

        if (checkChange) {
            model.addAttribute("messageChangePassword", "Mật khẩu của bạn đã được đổi thành công!");
            return "users/changePassword";
        } else {
            model.addAttribute("messageChangePassword", "Đã xảy ra lỗi khi đổi mật khẩu. Vui lòng thử lại.");
            return "users/changePassword";
        }
    }
}
