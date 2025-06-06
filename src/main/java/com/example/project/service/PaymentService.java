package com.example.project.service;

import com.example.project.model.*;
import com.example.project.repo.CourseRepo;
import com.example.project.repo.PaymentRepo;
import com.example.project.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    PaymentRepo paymentRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    CourseRepo courseRepo;

    public void addPayment(Users currentUser, Courses course) {
        Payments newPayments = new Payments();
        newPayments.setUser(currentUser);
        newPayments.setCourse(course);
        paymentRepo.save(newPayments);
    }

    public boolean hasUserPaidForCourse(Users user, Courses course) {
        return paymentRepo.existsByUserAndCourse(user, course);
    }

    public List<Payments> getPaymentsByUser(Users user) {
        // Bạn nên kiểm tra null cho đối tượng user trước khi truyền vào repository
        if (user == null) {
            return Collections.emptyList(); // Trả về danh sách rỗng nếu user là null
        }
        return paymentRepo.findByUser(user);
    }
}
