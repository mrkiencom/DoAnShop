package com.example.project.service;

import com.example.project.model.*;
import com.example.project.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;

    public Users getUserById(Integer userId) {
        Optional<Users> optionalCourse = userRepo.findById(userId);
        return optionalCourse.orElse(null);
    }

    public void saveUser(Users user) {
        userRepo.save(user);
    }

    public void updateUserPictureUrl(Integer userId, String pictureUrl) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        user.setPicture(pictureUrl); // Giả định UserAccount entity có trường 'picture' và setter
        userRepo.save(user);
    }
}
