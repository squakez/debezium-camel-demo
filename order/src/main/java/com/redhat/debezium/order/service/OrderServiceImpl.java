package com.redhat.debezium.order.service;

import java.util.Optional;

import com.redhat.debezium.order.domain.Order;
import com.redhat.debezium.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Override
    public void createOrder(Order order) {
        orderRepo.save(order);
    }

    @Override
    public Optional<Order> getOrder(Integer orderId) {
        return orderRepo.findById(orderId);
    }

    @Override
    public void updateOrder(Order order) {
        orderRepo.save(order);
    }

    @Override
    public void deleteOrder(Order order) {
        orderRepo.delete(order);
    }

    @Override
    public void addItem(Order order, String item) {
        order.addItem(item);
        orderRepo.save(order);
    }

    @Override
    public void deleteItem(Order order, String item) {
        order.deleteItem(item);
        orderRepo.save(order);
    }
}
