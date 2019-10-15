package com.redhat.debezium.demo.user.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.redhat.debezium.demo.user.domain.User;
import com.redhat.debezium.demo.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public void createUser(User user) {
        userRepo.save(user);
    }

    @Override
    public Optional<User> getUser(Integer userId) {
        return userRepo.findById(userId);
    }

    @Override
    public void updateUser(User user) {
        userRepo.save(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepo.delete(user);
    }

    @Override
    public void addOrder(User user, String orderId) {
        user.addOrder(orderId);
        userRepo.save(user);
    }

    @Override
    public void deleteOrder(User user, String orderId) {
        user.deleteOrder(orderId);
    }

    @Override
    public int countOrders(User user) {
        return user.getOrders().size();
    }

    @Override
    public List<User> allUsers() {
        return StreamSupport.stream(userRepo.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}
