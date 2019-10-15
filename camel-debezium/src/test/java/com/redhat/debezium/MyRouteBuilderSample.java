package com.redhat.debezium;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rest.swagger.RestSwaggerEndpoint;

/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilderSample extends RouteBuilder {

    public void configure() {
        from("timer:test?period=10s")
                .setBody(simple("{\"user_id\":1,\"order_id\":1}"))
                .process(new AfterStructToOrderTranslator())
                .to("rest-swagger:http://localhost:8082/v2/api-docs#addOrderUsingPOST")
                .log("Response : ${body}");
        RestSwaggerEndpoint e;
    }

}
