package com.example.project.controller;

import com.example.project.model.UserPrincipal;
import com.example.project.model.Users;
import com.example.project.service.AuthenticationService;
import com.example.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("user", new Users());
        return "signup";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") Users user, Model model) {
        boolean registrationResult = authenticationService.registerNewUserAccount(user);
        if (registrationResult) {
            return "redirect:/login?registrationSuccess";
        } else {
            model.addAttribute("registrationError", true);
            return "signup";
        }
    }
    @GetMapping("/adminPage")
    public String adminPage(){
        return "adminPage";
    }

    @PostMapping("/change_password")
    public String changePassword(Model model, @ModelAttribute("changePasswordDto") Users updateUser) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        Users user = new Users();

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                user = authenticationService.getInforUserByGmail(gmail);

                if (user != null) {
                    model.addAttribute("user_account", user);
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername();

                user = authenticationService.getInforUser(username);

                if (user != null) {
                    model.addAttribute("user_account", user);
                }
            }
        }

        Users existingUser = userService.getUserById(updateUser.getId());
        boolean checkChange = authenticationService.changeUserPassword(existingUser, updateUser.getPassword());

        if (checkChange) {
            model.addAttribute("messageChangePassword", "Mật khẩu của bạn đã được đổi thành công!");
            return "changePassword";
        } else {
            model.addAttribute("messageChangePassword", "Đã xảy ra lỗi khi đổi mật khẩu. Vui lòng thử lại.");
            return "changePassword";
        }
    }
}
