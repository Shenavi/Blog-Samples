##Creating the database

This sample is written against a mysql database.
* Login to your mysql database
* Create a database named "pizzadb"
* Create a table named pizza

CREATE TABLE `pizza` (
  `id` varchar(100) NOT NULL DEFAULT '',
  `pizzaName` varchar(200) DEFAULT NULL,
  `pizzaType` varchar(100) DEFAULT NULL,
  `pizzaPrice` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

* Enter the following records into the pizza table

insert into pizza values(105,"Chicken Pizza", "Chicken","$12.95");
insert into pizza values(107,"Cheese Pizza", "Veg","$11.95")
insert into pizza values(107,"Tomato Pizza", "Veg","$13.95");

##Running the service

* Navigate to the folder Pizza-Service after checking out the code.
* Build the code by running "mvn clean install"
* navigate to the target folder after you have completed building the service.
* Run the following command inside the target folder

```java -jar Pizza-Service-0.1-SNAPSHOT.jar```

* You should see the below output once you start the service running the command above

```
INFO  MicroservicesRegistryImpl:79 - Added microservice: org.wso2.pizza.service.PizzaService@7d6f77cc
INFO  NettyListener:68 - Starting Netty Http Transport Listener
INFO  NettyListener:110 - Netty Listener starting on port 8080
```
