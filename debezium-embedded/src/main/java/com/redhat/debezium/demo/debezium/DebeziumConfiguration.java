package com.redhat.debezium.demo.debezium;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.debezium.config.Configuration;
import io.debezium.embedded.EmbeddedEngine;
import io.debezium.embedded.spi.OffsetCommitPolicy;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

public class DebeziumConfiguration {

    public void init() {
        // Define the configuration for the embedded and MySQL connector ...
        Configuration config = Configuration.create()
                .with("connector.class",
                        "io.debezium.connector.mysql.MySqlConnector")
                .with("offset.storage",
                        "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename",
                        "/tmp/offset.dat")
                .with("offset.flush.interval.ms", 15000)
                /* begin connector properties */
                .with("name", "my-sql-connector")
                .with("database.hostname", "localhost")
                .with("database.port", 3306)
                .with("database.user", "debezium")
                .with("database.password", "dbz")
                .with("database.server.id", 1)
                .with("database.server.name", "my-app-connector")
                .with("database.history",
                        "io.debezium.relational.history.FileDatabaseHistory")
                .with("database.history.file.filename",
                        "/tmp/dbhistory.dat")
                .build();

        // Create the engine with this configuration ...
        EmbeddedEngine engine = EmbeddedEngine.create()
                .using(config)
                .using(OffsetCommitPolicy.always())
                .notifying(this::handleEvent)
                .build();

        // Run the engine asynchronously ...
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(engine);
    }

    private void handleEvent(List<SourceRecord> sourceRecords, EmbeddedEngine.RecordCommitter recordCommitter) {
        /*
        Struct{order_id=1,items=,order_price=100,user_id=0}
         */
        System.out.println(sourceRecords);
        for(SourceRecord sourceRecord:sourceRecords){
            Struct struct = (Struct) sourceRecord.value();
            try {
                Struct before = struct.getStruct("before");
                Struct after = struct.getStruct("after");
                if(before==null&&after!=null){
                    // new order inserted
                    String jsonOrderCreated = convertToJsonOrder(after);
                    System.out.println("New order received: "+jsonOrderCreated);
                    callUserAPIPost(after.getInt32("user_id"),jsonOrderCreated);
                }else if(before!=null&&after!=null){
                    // order updated
                    String jsonOrderBefore = convertToJsonOrder(before);
                    String jsonOrderAfter = convertToJsonOrder(after);
                    System.out.println("Order updated: "+jsonOrderBefore+" --> "+jsonOrderAfter);
                }else if(before!=null&&after==null){
                    // order deleted
                    String jsonOrderDeleted = convertToJsonOrder(before);
                    System.out.println("Order deleted: "+jsonOrderDeleted);
                    callUserAPIDelete(before.getInt32("user_id"),before.getInt32("order_id"));
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            try {
                recordCommitter.markProcessed(sourceRecord);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        recordCommitter.markBatchFinished();
    }

    private String convertToJsonOrder(Struct after) {
        int orderId = after.getInt32("order_id");
        int userId = after.getInt32("user_id");
        int orderPrice = after.getInt32("order_price");
        String items = after.getString("items");

        return String.format("{\"orderId\":%d,\"userId\":%d,\"orderPrice\":%d,\"items\":\"%s\"}",orderId,userId,orderPrice,items);
    }

    private void callUserAPIPost(int userId, String jsonOrder) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8082/user/"+userId+"/order/");

        StringEntity entity = new StringEntity(jsonOrder);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);
        System.out.println("Rest call:"+response);
        client.close();
    }

    private void callUserAPIDelete(int userId, int orderId) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete("http://localhost:8082/user/"+userId+"/order/"+orderId);
        CloseableHttpResponse response = client.execute(httpDelete);
        System.out.println("Rest call:"+response);
        client.close();
    }

}
