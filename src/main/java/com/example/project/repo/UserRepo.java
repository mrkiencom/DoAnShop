package com.example.project.repo;

import com.example.project.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {
    Users findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByGmail(String gmail);

    Users findByGmail(String gmail);

    Optional<Users> findById(Integer id);


    @Query("""
            select u from Users u where
            (:time is null or u.createdAt >= :time)
            and u.role <> 'admin'
             order by u.createdAt,u.firstname
            """)
    List<Users> findUserByTimeRage(LocalDateTime time);


    @Query("""
                select u from Users u where u.role <> 'admin'
            """)
    List<Users> findOnlyUsers();
}
