package com.redhat.debezium.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.debezium.order.domain.Order;
import com.redhat.debezium.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderRestController {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private OrderService orderService;

    @GetMapping("/{orderId}")
    public Order get(@PathVariable int orderId){
        return orderService.getOrder(orderId).get();
    }

    @PostMapping
    public Order create(@RequestBody Order order) {
        orderService.createOrder(order);
        return order;
    }

    @DeleteMapping("/{orderId}")
    public void delete(@PathVariable int orderId) {
        Order order = get(orderId);
        orderService.deleteOrder(order);
    }

    @PostMapping("/{orderId}/item")
    public void addItem(@PathVariable int orderId, @RequestBody String item){
        Order order = get(orderId);
        orderService.addItem(order, item);
        orderService.updateOrder(order);
    }

    @DeleteMapping("/{orderId}/item")
    public void deleteItem(@PathVariable int orderId, @RequestBody String item){
        Order order = get(orderId);
        orderService.deleteItem(order, item);
        orderService.updateOrder(order);
    }
}
