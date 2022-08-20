# How to set up containers for local development

## Starting from command line

### 1. Testnet
Firstly you must create persistent bitcoin volume.
```
docker volume create bitcoin
```
After that you can start containers by running: 
```
docker-compose -f devtools/docker/docker-compose.yaml -f devtools/docker/docker-compose-testnet.yaml up
```

### 2. Regtest
```
docker-compose -f devtools/docker/docker-compose.yaml -f devtools/docker/docker-compose-regtest.yaml up
```

## Container with LNPayments

It is possible to start container with bundled webservice and webapp. It can be done by the following command:

```
docker-compose -f devtools/docker/docker-compose-lnpayments.yaml -f up
```

Remember that you must still put macaroon and tls files in your home root directory. Instructions
can be found in webservice readme.

## Starting from IntelliJ
When you create new launch configuration in idea, you will have to select 2 files in _docker compose files_ section.
