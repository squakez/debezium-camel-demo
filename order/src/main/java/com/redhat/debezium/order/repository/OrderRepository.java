package com.redhat.debezium.order.repository;

import com.redhat.debezium.order.domain.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Integer> {
}
