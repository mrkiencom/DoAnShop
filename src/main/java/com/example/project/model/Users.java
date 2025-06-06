package com.example.project.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class Users {
    @Id
    private int id;
    private String username;
    private String password;
    private String gmail;
    private String picture;
    private String role;
    private String firstname;
    private String lastname;

    // Quan hệ một-nhiều với bảng Courses (một giảng viên có nhiều khóa học)
    @OneToMany(mappedBy = "lecturer")
    private List<Courses> createdCourses;

    public Users() {
    }

    public Users(String username, String password, String gmail,String picture, String role, String firstname, String lastname) {
        this.username = username;
        this.password = password;
        this.gmail = gmail;
        this.picture = picture;
        this.role = role;
        this.firstname = firstname;
        this.lastname = lastname;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public List<Courses> getCreatedCourses() {
        return createdCourses;
    }

    public void setCreatedCourses(List<Courses> createdCourses) {
        this.createdCourses = createdCourses;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", gmail='" + gmail + '\'' +
                ", picture='" + picture + '\'' +
                ", role='" + role + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                '}';
    }
}
