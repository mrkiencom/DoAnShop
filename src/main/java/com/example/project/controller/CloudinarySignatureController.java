package com.example.project.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator; // Import Comparator

@RestController
@RequestMapping("/api/cloudinary")
public class CloudinarySignatureController {

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Value("${cloudinary.cloud-name}")
    private String cloudName; // Cũng cần cloud name để trả về cho frontend

    @GetMapping("/sign-upload")
    public ResponseEntity<Map<String, String>> signUpload(
            @RequestParam(required = false, defaultValue = "user_profiles") String folder,
            @RequestParam(required = false) String publicId // Có thể nhận public_id nếu muốn đặt
    ) throws NoSuchAlgorithmException {

        long timestamp = System.currentTimeMillis() / 1000L; // Thời gian hiện tại theo giây

        Map<String, Object> params = new HashMap<>();
        params.put("timestamp", timestamp);
        params.put("folder", folder);
        if (publicId != null && !publicId.isEmpty()) {
            params.put("public_id", publicId);
        }
        // Thêm bất kỳ tham số upload nào khác mà bạn muốn bảo vệ bằng chữ ký
        // Ví dụ: params.put("eager", "w_400,h_300,c_fill"); // nếu muốn eager transformations

        // Sắp xếp các tham số theo thứ tự bảng chữ cái và nối chúng lại
        // Để tạo chuỗi cho chữ ký (signature)
        StringBuilder stringToSign = new StringBuilder();
        params.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey)) // Sắp xếp theo key
                .forEach(entry -> {
                    if (stringToSign.length() > 0) {
                        stringToSign.append("&");
                    }
                    stringToSign.append(entry.getKey()).append("=").append(entry.getValue());
                });

        stringToSign.append(apiSecret); // Nối API Secret vào cuối

        // Tạo chữ ký SHA-1
        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(stringToSign.toString().getBytes());
        String signature = byteToHex(crypt.digest());

        Map<String, String> response = new HashMap<>();
        response.put("signature", signature);
        response.put("timestamp", String.valueOf(timestamp));
        response.put("api_key", apiKey);
        response.put("cloud_name", cloudName); // Trả về cloud_name để frontend sử dụng

        return ResponseEntity.ok(response);
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    @GetMapping("/course-upload")
    public ResponseEntity<Map<String, String>> courseUpload(
            @RequestParam(required = false, defaultValue = "course_images") String folder,
            @RequestParam(required = false) String publicId
    ) throws NoSuchAlgorithmException {

        long timestamp = System.currentTimeMillis() / 1000L;

        Map<String, Object> params = new HashMap<>();
        params.put("timestamp", timestamp);
        params.put("folder", folder);
        if (publicId != null && !publicId.isEmpty()) {
            params.put("public_id", publicId);
        }

        StringBuilder stringToSign = new StringBuilder();
        params.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(entry -> {
                    if (stringToSign.length() > 0) {
                        stringToSign.append("&");
                    }
                    stringToSign.append(entry.getKey()).append("=").append(entry.getValue());
                });

        stringToSign.append(apiSecret);

        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(stringToSign.toString().getBytes());
        String signature = byteToHex(crypt.digest());

        Map<String, String> response = new HashMap<>();
        response.put("signature", signature);
        response.put("timestamp", String.valueOf(timestamp));
        response.put("api_key", apiKey);
        response.put("cloud_name", cloudName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/video-upload")
    public ResponseEntity<Map<String, String>> signUploadVideo(
            @RequestParam(name = "folder", defaultValue = "course_videos") String folder,
            @RequestParam(name = "publicId", required = false) String publicId,
            @RequestParam(name = "resourceType", defaultValue = "video") String resourceType
    ) throws NoSuchAlgorithmException {
        long timestamp = System.currentTimeMillis() / 1000L;

        Map<String, Object> params = new HashMap<>();
        params.put("folder", folder);
        if (publicId != null && !publicId.isEmpty()) {
            params.put("public_id", publicId);
        }
        params.put("timestamp", timestamp);

        // 🛠 Không ký resource_type ở đây!
        StringBuilder stringToSign = new StringBuilder();
        params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    if (stringToSign.length() > 0) {
                        stringToSign.append("&");
                    }
                    stringToSign.append(entry.getKey()).append("=").append(entry.getValue());
                });

        System.out.println("DEBUG: String to Sign = " + stringToSign.toString());

        // ✅ Ký với API secret
        stringToSign.append(apiSecret);
        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.update(stringToSign.toString().getBytes());
        String signature = byteToHex(crypt.digest());

        Map<String, String> response = new HashMap<>();
        response.put("signature", signature);
        response.put("timestamp", String.valueOf(timestamp));
        response.put("api_key", apiKey);
        response.put("cloud_name", cloudName);
        response.put("resource_type", resourceType); // vẫn trả về để frontend biết dùng
        response.put("folder", folder);
        response.put("public_id", publicId);

        return ResponseEntity.ok(response);
    }
}
