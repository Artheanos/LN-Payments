# How to set up containers for local development
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

## Starting from intellij
When you create new launch configuration in idea, you will have to select 2 files in _docker compose files_ section.
