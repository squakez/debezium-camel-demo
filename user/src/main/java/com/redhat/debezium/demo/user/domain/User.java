package com.redhat.debezium.demo.user.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.redhat.debezium.demo.user.repository.StringListConverter;
import lombok.Data;

@Data
@Entity
public class User {

    @Id
    private int userId;
    private String userName;
    @Convert(converter = StringListConverter.class)
    private List<String> orders = new ArrayList<>();

    public void addOrder(String orderId) {
        orders.add(orderId);
    }

    public void deleteOrder(String orderId) {
        orders.remove(orderId);
    }
}