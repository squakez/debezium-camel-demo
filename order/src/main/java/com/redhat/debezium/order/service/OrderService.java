package com.redhat.debezium.order.service;

import java.util.Optional;

import com.redhat.debezium.order.domain.Order;

public interface OrderService {

    void createOrder(Order order);

    Optional<Order> getOrder(Integer orderId);

    void updateOrder(Order order);

    void deleteOrder(Order order);

    void addItem(Order order, String item);

    void deleteItem(Order order, String item);

}
