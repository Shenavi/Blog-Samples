# Mutual SSL authentication between Kafka and WSO2 Streaming Integrator

## Introduction

When authenticating using SSL, private-key/certificate pairs are used during the SSL handshake process. In order to establish the trust between the client and the Kafka broker:

* Each broker needs its own private-key/certificate pair, and the client uses the certificate to authenticate the broker.
* Each logical client needs a private-key/certificate pair if client authentication is enabled, and the broker uses the certificate to authenticate the client.

Let's discuss the steps on setting this up and connecting to it through WSO2 Streaming Integrator.

## Configuring Kafka 

1. Download Kafka from [here](https://archive.apache.org/dist/kafka/2.3.0/kafka_2.12-2.3.0.tgz).

2. After downloading extract it to your machine. I am going to refer to the extracted folder as "KAFKA_HOME".

3. You can follow this [tutorial](https://docs.confluent.io/platform/current/security/security_tutorial.html#generating-keys-certs) and create the certificates needed. After creating the certificates, you will have these certificates with you. I am going to refer to the folder which has these certs as KAFKA_CERT_FOLDER

    *  kafka.server.keystore.jks - The Kafka Server's Keystore
    * kafka.server.truststore.jks - The Kafka Server's trustore
    * kafka.client.truststore.jks - The Client truststore which already consists the Kafka keystore's public certificate.

4. Now let's create the client keystore which we will configure in the WSO2 Streaming Integrator. Follow the below commands to create a keystore for the client.

    4.1 Navigate through the terminal to the KAFKA_CERT_FOLDER which you created above to store the Kafka Server's keys.

    4.2 Create the client keystore using the below command.

    ```
   keytool -genkey -keyalg RSA -validity 365 -alias kafka-client -keypass Admin1234 -storepass Admin1234 -dname "cn=localhost, ou=SE, o=WSO2, c=US" -keystore kafka.client.keystore.jks
    ```

   4.3 Next we need to export the public key of this keystore to import into the Kafka truststore. Use the below command to export the key.

   ```
   keytool -export -alias kafka-client -file "client.pem" -storepass Admin1234 -keystore kafka.client.keystore.jks   
   ```
   4.4 Now import the public certificate into the kafka truststore using the below command

   ```
   keytool -keystore kafka.server.truststore.jks -alias kafka-client -importcert -file client.pem
   ```
5. Now we can configure the Kafka server to authenticate using SSL. For that you need to update the server.properties file found in the location KAFKA_HOME/config as shown below. You can also refer this [document](https://docs.confluent.io/platform/current/kafka/authentication_ssl.html#brokers) to configure the server.properties file of the Broker.

    ```
    listeners=SSL://localhost:9092
    security.inter.broker.protocol=SSL
    ssl.client.auth=required
    ssl.truststore.location={KAFKA_CERT_FOLDER}/kafka.server.truststore.jks
    ssl.truststore.password=Admin1234
    ssl.keystore.location={KAFKA_CERT_FOLDER}/kafka.server.keystore.jks
    ssl.keystore.password=Admin1234
    ssl.key.password=Admin1234
    ```

Now our configurations are done.

6. Let's start the Zookeeper instance. 

    7.1 In a new terminal navigate to the location of the Kafka installation folder.
    ```
    cd KAFKA_HOME
    ```
    7.2 Start the zookeeper instance

    ```
    sh bin/zookeeper-server-start.sh config/zookeeper.properties
    ```

7. Next let's start the Kafka Broker.

    8.1 In a new terminal navigate to the location of the Kafka installation folder.
    ```
    cd KAFKA_HOME
    ```
    8.2 Start the Kafka broker instance

    ```
    sh bin/kafka-server-start.sh config/server.properties
    ```

## Creating Kafka Resources

### Prerequisites

In order to create a Kafka topic we need to now pass the required authenticationg from the client. Therefore let's create a authentication properties file which we can pass when creating a topic with Kafka.

1. Create a file name ssl-user-config.properties in the location KAFKA_HOME/config and paste the below content to it. These are the information required by Kafka in order to authenticate clients.

```
security.protocol=SSL
ssl.truststore.location={KAFKA_CERT_FOLDER}/kafka.client.truststore.jks
ssl.truststore.password=Admin1234
ssl.keystore.location={KAFKA_CERT_FOLDER}/kafka.client.keystore.jks
ssl.keystore.password=Admin1234
ssl.key.password=Admin1234
```

### Creating the productions topic

1. Navigate to the KAFKA_HOME location and execute the below command. We will be creating a topic named "productions". We will be consuming messages coming from this topic through the WSO2 Streaming integrator. Since our Kafka broker is secured with SSL authentication we need to pass the client configuration in order to create a topic.

```
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic production --command-config ./config/ssl-user-config.properties
```

### Creating the console producer for the production topic

1. Navigate to the KAFKA_HOME location and execute the below command. We will be creating a console producer to sent event to the production topic.

```
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic production --producer.config ./config/ssl-user-config.properties
```

## Setting up the Streaming Integrator

1. Download the Streaming Integrator from [here](https://wso2.com/integration/streaming-integrator/). I will refer to the extracted folder as STREAMING_INTEGRATOR_HOME.

2. Follow the "Before you begin" section of [this doc](https://ei.docs.wso2.com/en/7.2.0/streaming-integrator/examples/working-with-kafka/) in order to add the necessary dependencies to your streaming integration installation.

3. Navigate to the STREAMING_INTEGRATOR_HOME/bin location and start the server

```
sh server.sh
```

## Testing the sample

1. Create a file named HelloKafka_SSL.siddhi and paste the  content found in [this file](https://github.com/Shenavi/Blog-Samples/tree/master/streaming-integrator-samples/kafka/authenticate_kafka_with_ssl/HelloKafka_SSL.siddhi). Here we are passing the authentication information under the "optional.configuration" attribute. This is what the optional.configuration will look like in this case

```
optional.configuration="""security.protocol:SSL,ssl.truststore.location:/Users/shenavidemel/Documents/try/certs/kafka.client.truststore.jks,ssl.truststore.password:Admin1234,ssl.keystore.location:/Users/shenavidemel/Documents/try/certs/kafka.client.keystore.jks,ssl.keystore.password:Admin1234,ssl.key.password:Admin1234""",
        
```

2. Copy the HelloKafka_SSL.siddhi file to the location KAFKA_HOME/deployment/siddhi-files location

3. If you look at the Streaming Integrator logs you should see this getting successfully deployed

4. Now go to the terminal tab where you created the "production" topic's console producer and send the following event.

```
{"event":{ "name":"Almond cookie", "amount":100.0}}
```

5. You should see a log simillar to the below on the Streaming Integrator log.

```
[2021-07-01 19:27:27,490]  INFO {io.siddhi.core.stream.output.sink.LogSink} - HelloKafka_SSL : OutputStream : Event{timestamp=1625147847490, data=[ALMOND COOKIE, 100.0], isExpired=false}
```

So there you go. You have successfully consumed from a Kafka topics by authenticating through Mutual SSL. If you provide any incorrect certificate passwords/paths you will be able to see an authentication failure in the communication on the WSO2 Streaming Integrator logs. Simillarly you can configure the same to publish to Kafka Topics as well.