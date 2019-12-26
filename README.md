# IPAAS Syndesis meets Debezium

Follow these instructions to see how the changes to an Order service are propagated to a User service. User and Order are independent microservices with independent databases, no microservice business logic has been harmed during the experiments!

I am using [minishift](https://www.okd.io/minishift/) as local openshift development cluster; [httpie](https://httpie.org/) as http CLI.

* Deploy [Debezium](https://debezium.io/) (version 0.10) on a local cluster: https://debezium.io/documentation/reference/1.0/operations/openshift.html. You will use `debezium-connect` pod to enable your change data capture.
The database instance is shared for simplicity, but you can use 2 separate instances.
* Deploy [Syndesis](https://syndesis.io/) (version 2.0) on a local cluster: https://syndesis.io/quickstart/
* Deploy `user` microservice pod
```
cd user
mvn clean fabric8:deploy -P openshift
```
* Deploy `order` microservice pod
```
cd order
mvn clean fabric8:deploy -P openshift
```
* Create a `Debezium` integration on `Syndesis` mapping any new order creation to a user REST _addOrder_ endpoint call and any order deletion to a user REST _deleteOrder_ endpoint call. Wait for the integration to be up and running on your local openshift.

_Create a debezium connection, setting kafka broker cluster URI_\
![image 1](/img/1-connection.png)

_Create a new integration with the debezium connection source: select the order table change data topic and the schema change topic_\
![image 2](/img/1-1-integration-subscribe.png)

_Select a conditional flow as destination source_\
![image 3](/img/2-integration-conditional-flow.png)

_Define the condition events you want to capture: an order create and an order delete_\
![image 4](/img/3-conditions.png)

_Conditions will look like these:_\
![image 5](/img/4-conditions-set.png)

_Select the create condition branch_\
![image 6](/img/5-create-condition.png)

_Set the action to perform when a new order is created: call the User REST API_\
![image 7](/img/6-user-api-addorder.png)

_Finally map the data coming from change data stream to the ones expected by the REST API_\
![image 8](/img/7-data-mapping.png)

You must repeat the process for the delete condition branch as well. Here I leave the [whole screencast](/img/demo-screencast.mp4) for all details.

* Create a user (host name can be different, checkout yours)
```
http POST http://user-syndesis-services.192.168.42.139.nip.io/user userId=123 userName=Foo

{
    "orders": [],
    "userId": 123,
    "userName": "Foo"
}
```
* Create a order
```
http POST http://order-syndesis-services.192.168.42.139.nip.io/order orderId=987 orderPrice=100 userId=123

{
    "items": [],
    "orderId": 987,
    "orderPrice": 100,
    "userId": 123
}
```
* Check the order list was updated correctly
```
http http://user-syndesis-services.192.168.42.139.nip.io/user/123

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
http DELETE http://order-syndesis-services.192.168.42.139.nip.io/order/987
```
* Check the order list was updated correctly
```
http http://user-syndesis-services.192.168.42.139.nip.io/user/123

{
    "orders": [],
    "userId": 123,
    "userName": "Foo"
}
```
* Stop the `debezium-connect` pod to simulate a downtime / network issue
* Create an order
```
http POST http://order-syndesis-services.192.168.42.139.nip.io/order orderId=988 orderPrice=100 userId=123

{
    "items": [],
    "orderId": 988,
    "orderPrice": 100,
    "userId": 123
}
```
* Check the order list is not yet updated
```
http http://user-syndesis-services.192.168.42.139.nip.io/user/123

{
    "orders": [],
    "userId": 123,
    "userName": "Foo"
}
```
* Start the `debezium-connect` pod again
* Wait a few seconds the process to start up and check the order list was updated correctly
```
http http://user-syndesis-services.192.168.42.139.nip.io/user/123

{
    "orders": [
        "988"
    ],
    "userId": 123,
    "userName": "Foo"
}
```
