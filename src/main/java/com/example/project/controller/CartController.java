package com.example.project.controller;

import com.example.project.model.*;
import com.example.project.service.AuthenticationService;
import com.example.project.service.CartService;
import com.example.project.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CartController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    CourseService courseService;

    @Autowired
    CartService cartService;

    @GetMapping("/cart")
    public String cart(Model model) {
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
        List<Carts> coursesListCart = cartService.getCourseByIdUser(user);
        model.addAttribute("coursesListCart", coursesListCart);

        int totalPrice = 0;
        for (Carts item : coursesListCart) {
            if (item.getCourse() != null && item.getCourse().getPrice() != null) {
                totalPrice += item.getCourse().getPrice();
            }
        }

        model.addAttribute("totalPrice", totalPrice);


        return "users/cartPage";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("courseId") Integer courseId,
                            Authentication authentication) {
        Object principal = authentication.getPrincipal();
        Users currentUser = new Users();

        // **Cách 1: Xử lý người dùng đăng nhập truyền thống (qua UserDetails)**
        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            currentUser = authenticationService.getInforUser(userPrincipal.getUsername());
        }
        // **Cách 2: Xử lý người dùng đăng nhập qua OAuth2 (Gmail/Google)**
        else if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

            currentUser = authenticationService.getInforUserByGmail(oauthToken.getPrincipal().getAttribute("email"));
        }

        Courses course = courseService.getCourseById(courseId);

        cartService.addItem(currentUser,course);
        return "redirect:/cart";
    }

    @GetMapping("/removeItemCart/{cartId}")
    public String removeItemCart(@PathVariable Integer cartId, Model model){
        cartService.deleteItemCartById(cartId);
        return "redirect:/cart";
    }
}
