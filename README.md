# Camel meets Debezium

Follow these instructions to see how the changes to an Order service are propagated to a User service. User and Order are independent microservices with independent databases, no microservice business logic has been harmed during the experiments!

I am using httpie as http CLI.

* Start mysql database
```
cd docker
docker-compose up
```
The database instance is shared for simplicity, but you can use 2 separate instances.
* Start user microservice
```
cd user
mvn spring-boot:run
```
* Start order microservice
```
cd order
mvn spring-boot:run
```
* Start camel debezium route process
```
cd camel-debezium
mvn exec:java -Dexec.mainClass="com.redhat.debezium.MainApp"
```
* Create a user
```
http POST http://localhost:8082/user userId=123 userName=Foo

{
    "orders": [],
    "userId": 123,
    "userName": "Foo"
}
```
* Create a order
```
http POST http://localhost:8081/order orderId=987 orderPrice=100 userId=123

{
    "items": [],
    "orderId": 987,
    "orderPrice": 100,
    "userId": 123
}
```
* Check the order list was updated correctly
```
http http://localhost:8082/user/123

{
    "orders": [
        "987"
    ],
    "userId": 123,
    "userName": "Foo"
}
```
* Delete a order
```
http DELETE http://localhost:8081/order/987
```
* Check the order list was updated correctly
```
http http://localhost:8082/user/123

{
    "orders": [],
    "userId": 123,
    "userName": "Foo"
}
```
* Stop the debezium process to simulate a downtime / network issue
```
CTRL+C on debezium process window
```
* Create an order
```
http POST http://localhost:8081/order orderId=988 orderPrice=100 userId=123

{
    "items": [],
    "orderId": 988,
    "orderPrice": 100,
    "userId": 123
}
```
* Check the order list is not yet updated
```
http http://localhost:8082/user/123

{
    "orders": [],
    "userId": 123,
    "userName": "Foo"
}
```
* Start the debezium process again
```
mvn exec:java -Dexec.mainClass="com.redhat.debezium.MainApp"
```
* Wait a few seconds the process to start up and check the order list was updated correctly
```
http http://localhost:8082/user/123

{
    "orders": [
        "988"
    ],
    "userId": 123,
    "userName": "Foo"
}
```
