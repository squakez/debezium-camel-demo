package com.redhat.debezium.demo.user.controller;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.debezium.demo.user.domain.User;
import com.redhat.debezium.demo.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserRestController {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public User get(@PathVariable int userId){
        return userService.getUser(userId).get();
    }

    @GetMapping
    public List<User> create() {
        return userService.allUsers();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        userService.createUser(user);
        return user;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable int userId){
        User user = get(userId);
        userService.deleteUser(user);
    }

    @PostMapping("/{userId}/order")
    public void addOrder(@PathVariable int userId, @RequestBody String order){
        System.out.println("addOrder("+userId+","+order+")");
        User user = get(userId);
        try {
            userService.addOrder(user, mapper.readTree(order).get("orderId").asText());
            userService.updateUser(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @DeleteMapping("/{userId}/order/{orderId}")
    public void deleteOrder(@PathVariable int userId, @PathVariable int orderId){
        System.out.println("deleteOrder("+userId+","+orderId+")");
        User user = get(userId);
        userService.deleteOrder(user, ""+orderId);
        userService.updateUser(user);
    }
}
