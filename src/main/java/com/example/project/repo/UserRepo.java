package com.example.project.repo;

import com.example.project.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {
    Users findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByGmail(String gmail);

    Users findByGmail(String gmail);

    Optional<Users> findById(Integer id);
    
}
