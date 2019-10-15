package com.redhat.debezium;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.debezium.DebeziumConstants;

/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {

    public void configure() {
        from("debezium:mysql?name=my-sql-connector"
                + "&databaseServerId=1"
                + "&databaseHostName=localhost"
                + "&databaseUser=debezium"
                + "&databasePassword=dbz"
                + "&databaseServerName=my-app-connector"
                + "&databaseHistoryFileName=/tmp/dbhistory.dat"
                + "&databaseWhitelist=debezium"
                + "&tableWhitelist=debezium._order"
                + "&offsetStorageFileName=/tmp/offset.dat")
                .choice()
                    .when(header(DebeziumConstants.HEADER_OPERATION).isEqualTo("c"))
                        .process(new AfterStructToOrderTranslator())
                        .to("rest-swagger:http://localhost:8082/v2/api-docs#addOrderUsingPOST")
                    .when(header(DebeziumConstants.HEADER_OPERATION).isEqualTo("d"))
                        .process(new BeforeStructToOrderTranslator())
                        .to("rest-swagger:http://localhost:8082/v2/api-docs#deleteOrderUsingDELETE")
                .log("Response : ${body}");
    }

}
