package com.example.project.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import com.example.project.config.PaymentConfig;
import com.example.project.model.Courses;
import com.example.project.model.Payments;
import com.example.project.model.UserPrincipal;
import com.example.project.model.Users;
import com.example.project.service.AuthenticationService;
import com.example.project.service.CartService;
import com.example.project.service.CourseService;
import com.example.project.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PaymentController {

    @Autowired
    CourseService courseService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    CartService cartService;

	@GetMapping("/create_payment/{courseId}")
	public RedirectView creatPayment(HttpServletRequest request,
                                     @PathVariable("courseId") Integer courseId,
                                     Authentication authentication) throws UnsupportedEncodingException{
		int price = courseService.getCourseById(courseId).getPrice();
        String orderType = "other";
        String vnp_TxnRef = PaymentConfig.getRandomNumber(8);
        String vnp_IpAddr = PaymentConfig.getIpAddress(request);

        String vnp_TmnCode = PaymentConfig.vnp_TmnCode;
        
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", PaymentConfig.vnp_Version);
        vnp_Params.put("vnp_Command", PaymentConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(price*100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang : " + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_ReturnUrl", PaymentConfig.vnp_ReturnUrl);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        
        cld.add(Calendar.MINUTE, 10);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = PaymentConfig.vnp_PayUrl + "?" + queryUrl;
        System.out.println(paymentUrl);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(paymentUrl);

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
        paymentService.addPayment(currentUser,course);
        cartService.deleteItemCartAfterPayment(currentUser,course);

        return redirectView;
	}

    @GetMapping("/successfulPayment")
    public String successfulPaymentPage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            Object principal = authentication.getPrincipal();

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                String gmail = oauthToken.getPrincipal().getAttribute("email");

                authenticationService.saveGmailAccount(gmail);

                Users user = authenticationService.getInforUserByGmail(gmail);

                if (user != null) {
                    model.addAttribute("photo_user", user.getPicture());
                }
            }
            else if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                Users user = authenticationService.getInforUser(username);

                if (user != null) {
                    model.addAttribute("photo_user", user.getPicture());
                }
            }
        }
        return "users/successfulPayment";
    }
    @GetMapping("/myCourse")
    public String myCourse(Model model) {

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
                String username = userPrincipal.getUsername(); // Lấy username từ UserPrincipal

                user = authenticationService.getInforUser(username);

                if (user != null) {
                    model.addAttribute("user_account", user);
                }
            }
        }

        List<Payments> myCourse = paymentService.getPaymentsByUser(user);
        model.addAttribute("myCourses", myCourse);

        return "users/yourCourse";
    }

    @PostMapping("/payForCart")
    public RedirectView processCheckout(@RequestParam("courseIds") List<Integer> courseIds,HttpServletRequest request
                                    ,Authentication authentication) throws UnsupportedEncodingException {
        // Bây giờ 'courseIds' chứa danh sách tất cả các ID khóa học từ giỏ hàng
        System.out.println("Đã nhận các ID khóa học để thanh toán: " + courseIds);

        int totalPrice = 0;
        for (int courseId : courseIds) {
            Courses course = courseService.getCourseById(courseId);
            totalPrice += course.getPrice();
        }
        String orderType = "other";
        String vnp_TxnRef = PaymentConfig.getRandomNumber(8);
        String vnp_IpAddr = PaymentConfig.getIpAddress(request);

        String vnp_TmnCode = PaymentConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", PaymentConfig.vnp_Version);
        vnp_Params.put("vnp_Command", PaymentConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(totalPrice*100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang : " + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_ReturnUrl", PaymentConfig.vnp_ReturnUrl);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 10);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = PaymentConfig.vnp_PayUrl + "?" + queryUrl;
        System.out.println(paymentUrl);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(paymentUrl);

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

        for (int courseId : courseIds) {
            Courses course = courseService.getCourseById(courseId);
            paymentService.addPayment(currentUser,course);
            cartService.deleteItemCartAfterPayment(currentUser,course);
        }

        return redirectView;
    }
}