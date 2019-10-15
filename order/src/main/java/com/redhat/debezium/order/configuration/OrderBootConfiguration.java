package com.redhat.debezium.order.configuration;

import com.redhat.debezium.order.service.OrderService;
import com.redhat.debezium.order.service.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
public class OrderBootConfiguration {

    @Bean
    public OrderService orderService(){
        return new OrderServiceImpl();
    }
}
