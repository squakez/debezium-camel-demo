package com.redhat.debezium;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.debezium.DebeziumConstants;

public class AfterStructToOrderTranslator implements Processor {

    private static final String EXPECTED_BODY_FORMAT = "{\"userId\":%d,\"orderId\":%d}";

    public void process(Exchange exchange) throws Exception {
        final Map value = exchange.getMessage().getBody(Map.class);
        // Convert and set body
        int userId = (int) value.get("user_id");
        int orderId = (int) value.get("order_id");

        exchange.getIn().setHeader("userId", userId);
        exchange.getIn().setHeader("orderId", orderId);
        exchange.getIn().setBody(String.format(EXPECTED_BODY_FORMAT, userId, orderId));
    }
}
