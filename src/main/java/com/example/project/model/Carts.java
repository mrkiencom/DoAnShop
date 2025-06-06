package com.example.project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart")
public class Carts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartId; // cart_id

    @ManyToOne
    @JoinColumn(name = "user_id") // user_id là khóa ngoại trỏ đến bảng users
    private Users user; // Đối tượng User sở hữu giỏ hàng này

    @ManyToOne
    @JoinColumn(name = "course_id") // course_id là khóa ngoại trỏ đến bảng courses
    private Courses course; // Đối tượng Course trong giỏ hàng

    @Column(name = "added_date")
    private LocalDateTime addedDate;


    // Constructors
    public Carts() {
        this.addedDate = LocalDateTime.now(); // Tự động set thời gian khi tạo mới
    }

    public Carts(Users user, Courses course) {
        this.user = user;
        this.course = course;
        this.addedDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Courses getCourse() {
        return course;
    }

    public void setCourse(Courses course) {
        this.course = course;
    }

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", course=" + (course != null ? course.getTitle() : "null") +
                '}';
    }
}