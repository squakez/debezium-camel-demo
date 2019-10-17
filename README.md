# Camel meets Debezium

Follow these instructions to see how the changes to an Order service are propagated to a User service. User and Order are independent microservices with independent databases!

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
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Date: Thu, 17 Oct 2019 12:44:59 GMT
Transfer-Encoding: chunked

{
    "orders": [],
    "userId": 123,
    "userName": "Foo"
}
```
* Create a order
```
http POST http://localhost:8081/order orderId=987 orderPrice=100 userId=123
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Date: Thu, 17 Oct 2019 12:46:02 GMT
Transfer-Encoding: chunked

{
    "items": [],
    "orderId": 987,
    "orderPrice": 100,
    "userId": 123
}
```
* Check the user list was updated correctly
```
http http://localhost:8082/user/123
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Date: Thu, 17 Oct 2019 12:46:22 GMT
Transfer-Encoding: chunked

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
HTTP/1.1 200 
Content-Length: 0
Date: Thu, 17 Oct 2019 12:46:51 GMT
```
* Check the user list was updated correctly
```
http http://localhost:8082/user/123
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Date: Thu, 17 Oct 2019 12:47:06 GMT
Transfer-Encoding: chunked

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
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Date: Thu, 17 Oct 2019 12:48:00 GMT
Transfer-Encoding: chunked

{
    "items": [],
    "orderId": 988,
    "orderPrice": 100,
    "userId": 123
}
```
* Check the user list is not yet updated
```
http http://localhost:8082/user/123
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Date: Thu, 17 Oct 2019 12:48:54 GMT
Transfer-Encoding: chunked

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
* Wait a few seconds the process to start up and check the user list was updated correctly
```
http http://localhost:8082/user/123
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Date: Thu, 17 Oct 2019 12:50:36 GMT
Transfer-Encoding: chunked

{
    "orders": [
        "988"
    ],
    "userId": 123,
    "userName": "Foo"
}
```
