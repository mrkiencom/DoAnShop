package com.example.project.service;

import com.example.project.model.*;
import com.example.project.repo.CartRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepo cartRepo;

    public Carts findByUserAndCourse(Users currentUser, Courses course) {
        return cartRepo.findCartItemByUserAndCourse(currentUser, course);
    }

    public void addItem(Users currentUser, Courses course) {
        Carts cart = findByUserAndCourse(currentUser, course);
        if(cart == null){
            Carts newCartItem = new Carts();
            newCartItem.setUser(currentUser);
            newCartItem.setCourse(course);
            cartRepo.save(newCartItem);
        }
    }

    public List<Carts> getCourseByIdUser(Users user) {
        return cartRepo.findByUser(user);
    }

    public void deleteItemCartById(Integer cartId) {
        cartRepo.deleteById(cartId);
    }

    public void deleteItemCartAfterPayment(Users currentUser, Courses course) {
        Carts cart = findByUserAndCourse(currentUser, course);
        if(cart != null){
            cartRepo.delete(cart);
        };
    }
}
