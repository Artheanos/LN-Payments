# Webservice

It is a server used in the whole LN Payments system. Consists of several HTTP and websocket endpoints that manage
payments, wallet actions, transactions and much more. Server is not intended to be used independently, just acts as a
backend for web and mobile applications. Communicates directly with _bitcoind_ and _LND_ for bitcoin and lightning 
related operations.

## API specification

There are two ways to check the api specification of the webservice. In _devtools/postman_ you can find the
postman collection containing examples for all the endpoints with example values. Collection can be imported by 
any postman version that supports v2.1 format. Another option is to check the specification using Swagger. In order
to do that you must have webservice running. You can access the openApi specification by entering
[api-docs url](http://localhost:8080/v2/api-docs). There is also a Swagger UI running that can be accessed
with [this url](http://localhost:8080/swagger-ui/index.html).

## Local environment setup

### Pre-requirements
* docker
* docker-compose
* Java 17 JDK
* Maven

### Set up

Firstly you must set up bitcoind lightning node and a PostgreSQL database instance. You can do it automatically 
using docker-compose files from _devtools/docker_ folder. You can start it either in testnet or in regtest networks.
Instructions on starting all the infrastructure for both networks can be found in README file of _devtools/docker_
directory.
 
Initial launch of _bitcoind_ on Testnet will require around an hour and will take up to 30 GB of disk space. When
the blockchain is fully downloaded, you must obtain a tls certificate and macaroon from lightning node. You can do it by
executing the following commands:
```bash
docker cp lnd:/root/.lnd/tls.cert ~/.lnpayments
docker cp lnd:/root/.lnd/admin.macaroon ~/.lnpayments
```

Right now you should be up and ready to boot up the server. Find Application.java class and launch the _main_ method. You can also run the application without any IDE by running:
```bash
mvn spring-boot:run
```
Initially there is an admin user created, so you can authenticate and test the application with postman for instance.
You can access the server by localhost address and 8080 port.

### Testing

Unit and integration tests are mixed together. It is currently not possible to launch them separately. To execute them
from command line type:
```bash
mvn test
```

You can also run unit test coverage checks with the following command. Report can we found in _target/site/jacoco_
folder.
```bash
mvn verify -P test-coverage
```

## Building executable file

Server can be build into the executable JAR file with the command:
```bash
mvn package
```
File will be generated inside _target_ directory with the name _webservice-X.jar_, where X is a version number. 
Generated executable can be run with:
```bash
java -jar target/webservice-X.jar
```
In addition to that, you can also generate webservice _jar_ file with webapp bundled in it. It is served as static 
files at root resource path. In order to generate an image, insert the following command:
```bash
mvn package -P package-frontend
```

## Building docker image

Module contains dockerfile that allow to build the docker image. Prebuild one is available in the docker hub under
the name _oskar117/lnpayments_. To generate an image you must first package the application with one of the commands
from previous section. After that, you can execute:
```bash
docker build -t lnpayments .
```
