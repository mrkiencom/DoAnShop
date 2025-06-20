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

@Controller
public class UserController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserService userService;

    @GetMapping("/profile")
    public String getImage(final Model model) {

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
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                user = authenticationService.getInforUser(username);

                if (user != null) {
                    model.addAttribute("user_account", user);
                }
            }
        }

        return "users/profilePage";
    }

    @GetMapping("/information")
    public String getInformation(final Model model) {

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
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                user = authenticationService.getInforUser(username);

                if (user != null) {
                    model.addAttribute("user_account", user);
                }
            }
        }

        model.addAttribute("updateInformation", new Users());

        return "users/informationPage";
    }

    @PostMapping("/information")
    public String updateInformation(final Model model, @ModelAttribute("updateInformation") final Users informationUser) {

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
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                user = authenticationService.getInforUser(username);

                if (user != null) {
                    model.addAttribute("user_account", user);
                }
            }
        }

        final Users existingUser = userService.getUserById(informationUser.getId());

        existingUser.setFirstname(informationUser.getFirstname());
        existingUser.setLastname(informationUser.getLastname());
        existingUser.setGmail(informationUser.getGmail());

        userService.saveUser(existingUser);

        model.addAttribute("messageSuccessfully", "Cập nhật thành công!");

        return "users/informationPage";
    }

    @GetMapping("/change_password")
    public String changePasswordPage(final Model model) {

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

        model.addAttribute("changePasswordDto", new Users());

        return "users/changePassword";
    }

    @GetMapping("/about")
    public String about(final Model model) {
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
                final String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                user = authenticationService.getInforUser(username);

                if (user != null) {
                    model.addAttribute("user_account", user);
                }
            }
        }

        return "about";
    }
}
