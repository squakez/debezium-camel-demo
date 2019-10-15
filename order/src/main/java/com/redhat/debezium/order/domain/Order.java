package com.redhat.debezium.order.domain;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import com.redhat.debezium.order.repository.StringListConverter;
import lombok.Data;

@Data
@Entity
@Table(name="_order")
public class Order {

    @Id
    private int orderId;
    @NotNull
    private int userId;
    private int orderPrice;
    @Convert(converter = StringListConverter.class)
    private List<String> items = new ArrayList<>();

    public void addItem(String itemId) {
        items.add(itemId);
    }

    public void deleteItem(String itemId) {
        items.remove(itemId);
    }
}