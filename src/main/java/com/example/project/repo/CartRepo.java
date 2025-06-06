package com.example.project.repo;

import com.example.project.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepo extends JpaRepository<Carts, Integer> {
    Carts findCartItemByUserAndCourse(Users user, Courses course);
    List<Carts> findByUser(Users user);
}
