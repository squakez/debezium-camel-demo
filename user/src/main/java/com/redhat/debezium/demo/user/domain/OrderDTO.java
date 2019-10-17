package com.redhat.debezium.demo.user.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class OrderDTO {

    private int orderId;
    private int userId;
}
