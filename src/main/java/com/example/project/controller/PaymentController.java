package com.example.project.controller;

import com.example.project.config.PaymentConfig;
import com.example.project.model.Courses;
import com.example.project.model.PaymentRequest;
import com.example.project.model.Payments;
import com.example.project.model.UserPrincipal;
import com.example.project.model.Users;
import com.example.project.repo.PaymentRequestRepo;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    @Autowired
    PaymentRequestRepo paymentRequestRepo;

    @GetMapping("/create_payment/{courseId}")
    public RedirectView createPayment(final HttpServletRequest request,
                                      @PathVariable("courseId") final Integer courseId,
                                      final Authentication authentication) throws UnsupportedEncodingException {
        final int price = courseService.getCourseById(courseId).getPrice();
        final String orderType = "other";
        final String vnp_TxnRef = PaymentConfig.getRandomNumber(8);
        final String vnp_IpAddr = PaymentConfig.getIpAddress(request);

        final String vnp_TmnCode = PaymentConfig.vnp_TmnCode;

        final Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", PaymentConfig.vnp_Version);
        vnp_Params.put("vnp_Command", PaymentConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(price * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang : " + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_ReturnUrl", PaymentConfig.vnp_ReturnUrl); // Ex: http://localhost:8080/c


        final ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        final ZonedDateTime createDate = ZonedDateTime.now(zoneId);
        final ZonedDateTime expireDate = createDate.plusMinutes(15);
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        vnp_Params.put("vnp_CreateDate", createDate.format(formatter));
        vnp_Params.put("vnp_ExpireDate", expireDate.format(formatter));

        final List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        final StringBuilder hashData = new StringBuilder();
        final StringBuilder query = new StringBuilder();

        for (final Iterator<String> itr = fieldNames.iterator(); itr.hasNext(); ) {
            final String fieldName = itr.next();
            final String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        final String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.secretKey, hashData.toString());
        final String paymentUrl = PaymentConfig.vnp_PayUrl + "?" + query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;

        // Lưu tạm PaymentRequest vào DB
        Users currentUser = null;
        if (authentication.getPrincipal() instanceof final UserPrincipal userPrincipal) {
            currentUser = authenticationService.getInforUser(userPrincipal.getUsername());
        } else if (authentication instanceof final OAuth2AuthenticationToken oauthToken) {
            final String gmail = oauthToken.getPrincipal().getAttribute("email");
            currentUser = authenticationService.getInforUserByGmail(gmail);
        }

        if (currentUser != null) {
            final PaymentRequest requestRecord = new PaymentRequest();
            requestRecord.setVnpTxnRef(vnp_TxnRef);
            requestRecord.setCourseId(courseId);
            requestRecord.setUserId(currentUser.getId());
            requestRecord.setStatus("pending");
            requestRecord.setCreatedAt(LocalDateTime.now());
            paymentRequestRepo.save(requestRecord);
        }

        return new RedirectView(paymentUrl);
    }

    @GetMapping("/vnpay_return")
    public String handleVnPayReturn(@RequestParam final Map<String, String> params, final Model model, final Authentication authentication) {
        final String responseCode = params.get("vnp_ResponseCode");
        final String txnRef = params.get("vnp_TxnRef");

        final List<PaymentRequest> requests = paymentRequestRepo.findAllByVnpTxnRef(txnRef);
        if (requests.isEmpty()) {
            model.addAttribute("message", "Không tìm thấy đơn hàng.");
            return "users/failedPayment";
        }

        if ("00".equals(responseCode)) {
            for (final PaymentRequest request : requests) {
                request.setStatus("success");
                paymentRequestRepo.save(request);

                final Users currentUser = authenticationService.findById(request.getUserId());
                final Courses course = courseService.getCourseById(request.getCourseId());

                paymentService.addPayment(currentUser, course);
                cartService.deleteItemCartAfterPayment(currentUser, course);
            }

            return "redirect:/successfulPayment";

        } else if ("24".equals(responseCode)) {
            for (final PaymentRequest request : requests) {
                request.setStatus("canceled");
                paymentRequestRepo.save(request);
            }
            return "redirect:/";
        } else {
            for (final PaymentRequest request : requests) {
                request.setStatus("failed");
                paymentRequestRepo.save(request);
            }
            return "users/failedPayment";
        }
    }


    @GetMapping("/successfulPayment")
    public String successfulPaymentPage(final Model model) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            final Object principal = authentication.getPrincipal();

            try {
                if (authentication instanceof final OAuth2AuthenticationToken oauthToken) {
                    final String gmail = oauthToken.getPrincipal().getAttribute("email");
                    authenticationService.saveGmailAccount(gmail);
                    final Users user = authenticationService.getInforUserByGmail(gmail);
                    if (user != null) {
                        model.addAttribute("photo_user", user.getPicture());
                        model.addAttribute("user_account", user);

                    }
                } else if (principal instanceof final UserPrincipal userPrincipal) {
                    final String username = userPrincipal.getUsername();
                    final Users user = authenticationService.getInforUser(username);
                    if (user != null) {
                        model.addAttribute("photo_user", user.getPicture());
                        model.addAttribute("user_account", user);
                    }
                }
            } catch (final Exception e) {
                // log lại nếu muốn
                System.out.println("Lỗi xử lý người dùng sau thanh toán: " + e.getMessage());
            }
        }

        return "users/successfulPayment"; // đảm bảo file này tồn tại
    }


    @GetMapping("/myCourse")
    public String myCourse(final Model model) {

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

        final List<Payments> myCourse = paymentService.getPaymentsByUser(user);
        model.addAttribute("myCourses", myCourse);

        return "users/yourCourse";
    }

    @PostMapping("/payForCart")
    public RedirectView processCheckout(@RequestParam("courseIds") final List<Integer> courseIds,
                                        final HttpServletRequest request,
                                        final Authentication authentication) throws UnsupportedEncodingException {

        int totalPrice = 0;
        for (final int courseId : courseIds) {
            final Courses course = courseService.getCourseById(courseId);
            totalPrice += course.getPrice();
        }

        final String orderType = "cart";
        final String vnp_TxnRef = PaymentConfig.getRandomNumber(8);
        final String vnp_IpAddr = PaymentConfig.getIpAddress(request);
        final String vnp_TmnCode = PaymentConfig.vnp_TmnCode;

        final Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", PaymentConfig.vnp_Version);
        vnp_Params.put("vnp_Command", PaymentConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(totalPrice * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan gio hang: " + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_ReturnUrl", PaymentConfig.vnp_ReturnUrl);

        final ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        final ZonedDateTime createDate = ZonedDateTime.now(zoneId);
        final ZonedDateTime expireDate = createDate.plusMinutes(15);
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        vnp_Params.put("vnp_CreateDate", createDate.format(formatter));
        vnp_Params.put("vnp_ExpireDate", expireDate.format(formatter));

        final List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        final StringBuilder hashData = new StringBuilder();
        final StringBuilder query = new StringBuilder();
        for (final Iterator<String> itr = fieldNames.iterator(); itr.hasNext(); ) {
            final String fieldName = itr.next();
            final String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        final String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.secretKey, hashData.toString());
        final String paymentUrl = PaymentConfig.vnp_PayUrl + "?" + query + "&vnp_SecureHash=" + vnp_SecureHash;

        Users currentUser = null;
        if (authentication.getPrincipal() instanceof final UserPrincipal userPrincipal) {
            currentUser = authenticationService.getInforUser(userPrincipal.getUsername());
        } else if (authentication instanceof final OAuth2AuthenticationToken oauthToken) {
            final String gmail = oauthToken.getPrincipal().getAttribute("email");
            currentUser = authenticationService.getInforUserByGmail(gmail);
        }

        if (currentUser != null) {
            final List<PaymentRequest> requests = new ArrayList<>();
            for (final int courseId : courseIds) {
                final PaymentRequest requestRecord = new PaymentRequest();
                requestRecord.setVnpTxnRef(vnp_TxnRef);
                requestRecord.setCourseId(courseId);
                requestRecord.setUserId(currentUser.getId());
                requestRecord.setStatus("pending");
                requestRecord.setCreatedAt(LocalDateTime.now());
                requests.add(requestRecord);
            }
            paymentRequestRepo.saveAll(requests);
        }

        return new RedirectView(paymentUrl);
    }

}