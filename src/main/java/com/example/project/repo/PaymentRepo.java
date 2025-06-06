package com.example.project.repo;

import com.example.project.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payments, Long> {
    boolean existsByUserAndCourse(Users user, Courses course);
    List<Payments> findByUser(Users user);
}
