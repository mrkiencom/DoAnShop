package com.example.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class CloudinarySignatureService {
    @Autowired
    private UserService userService;

    @PatchMapping("/{userId}/update-picture-url")
    public ResponseEntity<Map<String, String>> updateProfilePictureUrl(
            @PathVariable Integer userId,
            @RequestBody Map<String, String> payload) {
        try {
            String pictureUrl = payload.get("pictureUrl");
            if (pictureUrl == null || pictureUrl.isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "URL ảnh không được cung cấp."), HttpStatus.BAD_REQUEST);
            }

            // Gọi service để cập nhật URL ảnh cho người dùng
            userService.updateUserPictureUrl(userId, pictureUrl);

            return new ResponseEntity<>(Map.of("message", "Cập nhật URL ảnh thành công."), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Lỗi khi cập nhật URL ảnh vào DB: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
