
# BigData CDC (Change Data Capture)

# Description
Custom some Debezium classes/libs  by your business logic

## How to use
### 1. Build
```shell
mvn clean package -DskipTests
```
### 2. Test with Module cdc-debezium-server
```shell
cd cdc-debezium-server 
run MainApp.java
```

### 2. Setup Debezium Server
Follow via https://debezium.io/documentation/reference/stable/operations/debezium-server.html

### Architecture
![alt text](./images/cdc_debezium_server.gif)
## Contributing
The project has a separate contribution file. Please adhere to the steps listed in the separate contributions [file](./CONTRIBUTING.md)
## License
[![Licence](https://img.shields.io/github/license/Ileriayo/markdown-badges?style=for-the-badge)](./LICENSE)
