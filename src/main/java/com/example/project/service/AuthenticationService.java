package com.example.project.service;

import com.example.project.model.Courses;
import com.example.project.model.Users;
import com.example.project.repo.UserRepo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepo userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepo userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registerNewUserAccount(Users user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return false; // Username đã tồn tại
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPicture("/images/default_user.jpg");
        user.setRole("user");
        userRepository.save(user);
        return true;
    }

    public void saveGmailAccount(String gmail) {
        boolean check = userRepository.existsByGmail(gmail);
        if(!check){
            Users newUser = new Users();
            newUser.setGmail(gmail);
            newUser.setPicture("/images/default_user.jpg");
            newUser.setRole("user");
            userRepository.save(newUser);
        }
    }

    public Users getInforUser(String username) {
        return userRepository.findByUsername(username);
    }

    public Users getInforUserByGmail(String gmail) {
        return userRepository.findByGmail(gmail);
    }

    public boolean changeUserPassword(Users user, String newPassword) {
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
