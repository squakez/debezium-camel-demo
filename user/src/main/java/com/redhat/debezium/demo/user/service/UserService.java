package com.redhat.debezium.demo.user.service;

import java.util.List;
import java.util.Optional;

import com.redhat.debezium.demo.user.domain.User;

public interface UserService {

    void createUser(User user);

    Optional<User> getUser(Integer userId);

    void updateUser(User user);

    void deleteUser(User user);

    void addOrder(User user, String orderId);

    void deleteOrder(User user, String orderId);

    int countOrders(User user);

    List<User> allUsers();
}
