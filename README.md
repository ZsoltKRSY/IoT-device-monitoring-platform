# IoT Device Monitoring Platform
Microservices-based IoT monitoring system built with Spring Boot, Angular, RabbitMQ, and Docker.  
Includes an API Gateway secured with JWT authentication and a standalone device data simulator capable of running multiple concurrent instances.

## Deployment with Docker
From a terminal opened in the root folder of the application, run:
```bash
docker compose up --build
```
Or run in detached mode:
```bash
docker compose up --build -d
```
All dependencies are automatically downloaded and built, including the databases if they do not already exist.  
Initialization .sql scripts are included in the project.

## Device Data Simulator
The Device Data Simulator is a standalone application and is not part of the Docker deployment. It must be started manually after all other services have been successfully deployed, especially:
- Devices Service
- RabbitMQ message broker

### Running a Single Instance
You can run a single instance directly from IntelliJ. Default server port:
```code
8090
```

### Running Multiple Instances
To run multiple instances, first build the JAR file from the root directory:
```bash
./mvnw clean package -DskipTests
```
Then start the application:
```bash
java -jar target/device_data_simulator-0.0.1-SNAPSHOT.jar --server.port=8090
```
You can execute this command multiple times in separate terminals.  
**Important:** Use a different server port for each instance.

### Simulator Behavior
- Automatically retrieves and updates the list of device IDs every 5 minutes
- Generates simulated values for 10 randomly selected devices

To manually generate device data, follow the console instructions:
- Enter the number of values to generate
- Enter the number of seconds to wait between each generated value

## Application Access
| Port  | Service                                                     |
| ----- | ----------------------------------------------------------- |
| 4200  | Angular Frontend (main user interface)                      |
| 15672 | RabbitMQ Dashboard (queue monitoring)                       |
| 8080  | API Gateway (JWT-protected endpoints, accessible from host) |
| 8081  | Devices Service (Swagger exposed)                           |
| 8082  | Users Service (Swagger exposed)                             |
| 8083  | Authentication Service (Swagger exposed)                    |
| 8084  | Monitoring Service (Swagger exposed)                        |

The last four services are exposed only for Swagger access.  
In a production environment, they should not be publicly exposed, as they communicate internally through the Docker network via the API Gateway.


### Access URLs
Frontend:
```bash
http://localhost:4200
```
RabbitMQ Dashboard:
```bash
http://localhost:15672
```
Swagger UI:
```bash
http://localhost:8081/swagger-ui/index.html
http://localhost:8082/swagger-ui/index.html
http://localhost:8083/swagger-ui/index.html
http://localhost:8084/swagger-ui/index.html
```
