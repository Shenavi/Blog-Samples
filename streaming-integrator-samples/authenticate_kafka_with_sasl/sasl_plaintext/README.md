# Username/Password based authentication between Kafka and WSO2 Streaming Integrator

## Introduction

In order to achieve this we are going to use the SASL Authentication which is configured using Java Authentication and Authorization Service (JAAS). JAAS is also used for authentication of connections between Kafka and ZooKeeper. Here we are going to use the SASL_PLAINTEXT method which uses username and password as the form of authentication. JAAS is also used for authentication of connections between Kafka and ZooKeeper. Let's discuss the steps on setting this up and connecting to it through WSO2 Streaming Integrator.

## Configuring Kafka 

1. Download Kafka from [here](https://archive.apache.org/dist/kafka/2.3.0/kafka_2.12-2.3.0.tgz).

2. After downloading extract it to your machine. I am going to refer to the extracted folder as "KAFKA_HOME".

3. Next let's create the JAAS configs for the Zookeeper and the Kafka Server.

4. Create a file name kafka_server_jaas.conf inside the location KAFKA_HOME/config/ folder and paste the below content to it.

```
KafkaServer {
  org.apache.kafka.common.security.plain.PlainLoginModule required
  serviceName="kafka"
  username="admin"
  password="12345"
  user_admin="12345";
};

Client {
  org.apache.kafka.common.security.plain.PlainLoginModule required
  username="admin"
  password="12345";
};
```


**NOTE :** When defining a new user you need to define it in the format user_{username}="{password}". In the example above the username is "admin" and the password is "12345".

5. Create a file named zookper_jaas.conf inside the location KAFKA_HOME/config/ folder and paste the below content to it.

```
Server {
org.apache.kafka.common.security.plain.PlainLoginModule required
username="admin"
password="12345"
user_admin="12345";
};
```
Once you have created these two files next we need to modify the Kafka Server properties to reflect this authentication method.

6. Open the file KAFKA_HOME/config/server.properties and update the listener configurations in this file.

```
# AUTH

security.inter.broker.protocol=SASL_PLAINTEXT
sasl.mechanism.inter.broker.protocol=PLAIN
sasl.enabled.mechanisms=PLAIN

authorizer.class.name=kafka.security.auth.SimpleAclAuthorizer
allow.everyone.if.no.acl.found=true
listeners=SASL_PLAINTEXT://0.0.0.0:9092
advertised.listeners=SASL_PLAINTEXT://:9092
```
Now our configurations are done.

7. Let's setup the Zookeeper instance. 

    7.1 In a new terminal navigate to the location of the Kafka installation folder.
    ```
    cd KAFKA_HOME
    ```
    7.2 Export the Zookeeper JAAS config as KAFKA options.

    ```
    export KAFKA_OPTS=”-Djava.security.auth.login.config={KAFKA_HOME}/config/zookeeper_jaas.conf”

    ```
    7.3 Start the zookeeper instance

    ```
    sh bin/zookeeper-server-start.sh config/zookeeper.properties
    ```

8. Next let's setup the Kafka Broker.

    8.1 In a new terminal navigate to the location of the Kafka installation folder.
    ```
    cd KAFKA_HOME
    ```
    8.2 Export the Kafka Server JAAS config as KAFKA options.

    ```
    export KAFKA_OPTS=”-Djava.security.auth.login.config={KAFKA_HOME}/config/kafka_server_jaas.conf”

    ```
    8.3 Start the Kafka broker instance

    ```
    sh bin/kafka-server-start.sh config/server.properties
    ```

## Creating Kafka Resources

### Prerequisites

In order to create a Kafka topic we need to now pass the required authenticationg from the client. Therefore let's create a authentication properties file which we can pass when creating a topic with Kafka.

1. Create a file name sasl-user-config.properties in the location KAFKA_HOME/config and paste the below content to it. These are the information required by Kafka in order to authenticate clients.

```
security.protocol=SASL_PLAINTEXT
sasl.mechanism=PLAIN
sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="admin" password="12345";
```

### Creating the sales topic

1. Navigate to the KAFKA_HOME location and execute the below command. We will be creating two topics named "sales" and "bulk-orders". The messages from the sales topic will be consumed through the WSO2 Streaming Integrator. And the processed messages will be published to the bulk-orders topic through the Streaming Integrator. (We will discuss this in more detail later)

```
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic sales --command-config ./config/sasl-user-config.properties
```

```
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic bulk-orders --command-config ./config/sasl-user-config.properties
```

### Creating the console producer for the sales topic

1. Navigate to the KAFKA_HOME location and execute the below command. We will be creating a console producer to sent event to the sales topic.

```
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic sales --producer.config ./config/sasl-user-config.properties
```

### Creating a Kafka console consumer

1. The events received through the sales topic will be modified through the WSO2 Streaming Integrator and pushed to the bulk-orders topic. In order to view the messaged pushed from WSO2 Streaming Integrator to the bulk-orders topic, let's create a console consumer.

```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic bulk-orders --from-beginning --consumer.config ./config/sasl-user-config.properties
```
Now we have created all the needed resources on the Kafka end. Next let's test this out through the WSO2 Streaming Integrator.

## Setting up the Streaming Integrator

1. Download the Streaming Integrator from [here](https://wso2.com/integration/streaming-integrator/). I will refer to the extracted folder as STREAMING_INTEGRATOR_HOME.

2. Follow the "Before you begin" section of [this doc](https://ei.docs.wso2.com/en/7.2.0/streaming-integrator/examples/working-with-kafka/) in order to add the necessary dependencies to your streaming integration installation.

3. Navigate to the STREAMING_INTEGRATOR_HOME/bin location and start the server

```
sh server.sh
```

## Testing the sample

1. Create a file named HelloKafka.siddhi and paste the  content found in [this file](https://github.com/Shenavi/Blog-Samples/tree/master/streaming-integrator-samples/authenticate_kafka_with_sasl/sasl_plaintext/HelloKafka.siddhi). Here we are passing the authentication information under the "optional.configuration" attribute. This is what the optional.configuration will look like in this case

```
optional.configuration="""security.protocol:SASL_PLAINTEXT,sasl.kerberos.service.name:kafka,sasl.mechanism:PLAIN,sasl.jaas.config:org.apache.kafka.common.security.plain.PlainLoginModule required username="admin" password="12345";""",
        
```

2. Copy the HelloKafka.siddhi file to the location KAFKA_HOME/deployment/siddhi-files location

3. If you look at the Streaming Integrator logs you should see this getting successfully deployed

4. Now go to the terminal tab where you created the "sales" console producer and send the following event.

```
{"event":{ "name":"computer", "quantity":5}}
```

5. Next open the terminal tab containing the "bulk-orders" console consumer and you will be able to see an out simillar to the below.

```
{"event":{"name":"computer","quantity":5,"timestamp":"2021-07-01 12:34:41"}}
```

So there you go. You have successfully consumed from and produced to Kafka topics by authenticating through a username and password. If you update the password in the siddhi application to a different value you will be able to see an authentication failure in the communication.