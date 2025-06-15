package com.example.project.repo;

import com.example.project.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
                select u from Users u 
                where (:#{#time == null} = true or u.createdAt >= :time)
                and u.role <> 'admin'
                order by u.createdAt, u.firstname
            """)
    List<Users> findUserByTimeRage(@Param("time") LocalDateTime time);


    @Query("""
                select u from Users u where u.role <> 'admin'
            """)
    List<Users> findOnlyUsers();

    @Query("""
                select u from Users u where
                (:filter is null 
                or u.role = :filter 
                or :filter = 'learner' and (u.role = 'user' or u.role = 'learner'))
                and (
                    :text is null 
                    or lower(concat(u.firstname, ' ', u.lastname)) like lower(concat('%', :text, '%'))
                    or lower(u.gmail) like lower(concat('%', :text, '%'))
                    or u.role = :text
                )
                and (
                    :status is null 
                    or (:status = 'active' and u.isActive = true)
                    or (:status = 'inactive' and u.isActive = false)
                )
            """)
    Page<Users> filterUsers(String filter, String status, String text, Pageable pageable);
}
