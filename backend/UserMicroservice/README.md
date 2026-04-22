
# UserMicroservice

A Spring Boot microservice for managing users (Clients, Sellers, Admins) in an online shop ecosystem. It supports CRUD operations, region and interest management, and integrates with RabbitMQ for event-driven communication.

---

## Features

- **User Types:** Client, Seller, Admin
- **CRUD Operations:** Create, Read, Update, Delete for Clients and Sellers
- **Interest & Region Management:** Assign interests and regions to users
- **RabbitMQ Integration:** Publishes user add/delete events
- **Spring Security:** Customizable (disabled in tests)
- **Database:** PostgreSQL (prod), H2 (test)
- **Lombok:** For concise DTO/entity code
- **Comprehensive Integration Tests:** Using MockMvc, Mockito, H2

---

## Technologies

- Java 21+ (or 25)
- Spring Boot 4.x
- Spring Data JPA
- Spring Security
- Spring AMQP (RabbitMQ)
- Lombok
- H2 (test), PostgreSQL (prod)
- Testcontainers (optional, for integration tests)
- Jackson 3.x (`tools.jackson`)
- Maven

---

## Endpoints

### Client

| Method | Path                          | Description            |
|--------|-------------------------------|------------------------|
| GET    | `/v1/users/client/profile`     | Get client profile     |
| PUT    | `/v1/users/client/profile`     | Update client profile  |
| DELETE | `/v1/users/client/profile`     | Delete client profile  |

**Headers:**
`UserId-Header: <UUID>`

#### Example: Get Client Profile
Request:
```http
GET /v1/users/client/profile HTTP/1.1
UserId-Header: 550e8400-e29b-41d4-a716-446655440000
```

Response:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "username": "johndoe",
  "region": "EU",
  "age": 25,
  "phoneNumber": "+40712345678",
  "interests": ["electronics", "books"]
}
```

### Seller

| Method | Path                          | Description            |
|--------|-------------------------------|------------------------|
| GET    | `/v1/users/seller/profile`     | Get seller profile     |
| PUT    | `/v1/users/seller/profile`     | Update seller profile  |
| DELETE | `/v1/users/seller/profile`     | Delete seller profile  |

**Headers:**
`UserId-Header: <UUID>`

#### Example: Get Seller Profile
Request:
```http
GET /v1/users/seller/profile HTTP/1.1
UserId-Header: 550e8400-e29b-41d4-a716-446655440000
```

Response:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "My Shop",
  "email": "seller@example.com",
  "username": "seller1",
  "logoURL": "https://example.com/logo.png",
  "description": "Best shop in town",
  "region": "EU"
}
```

### Admin

| Method | Path                          | Description            |
|--------|-------------------------------|------------------------|
| GET    | `/v1/users/admin/profile`      | Get admin profile      |
| PUT    | `/v1/users/admin/profile`      | Update admin profile   |
| DELETE | `/v1/users/admin/profile`      | Delete admin profile   |

**Headers:**
`UserId-Header: <UUID>`

---

## RabbitMQ Integration

- Publishes user add/delete events to topic exchanges:
    - `addUser-Topic-Exchange`
    - `deleteUser-Topic-Exchange`
- Routing keys: `addUser.admin`, `addUser.seller`, `addUser.client`, `deleteUser`

### Producer (sends messages)

On user creation/deletion, a message is published to the appropriate topic exchange with the routing key:

| Routing Key       | Message DTO         | Description                  |
|-------------------|---------------------|------------------------------|
| `addUser.admin`   | `AdminMessageDTO`   | Admin added                  |
| `addUser.seller`  | `SellerMessageDTO`  | Seller added                 |
| `addUser.client`  | `ClientMessageDTO`  | Client added                 |
| `deleteUser`      | `DeleteUserDTO`     | User deleted                 |

#### Example: Delete User Event
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Consumer (receives messages)

| Queue                                           | Routing Key    | Description                          |
|-------------------------------------------------|----------------|--------------------------------------|
| `deleteUser-Topic-Exchange.user-microservice`   | `deleteUser`   | Deletes user by UUID from DB         |

---

## Environment Variables

### Database
| Variable       | Default     | Description                |
|----------------|-------------|----------------------------|
| `DB_IP`        | `localhost` | PostgreSQL host IP         |
| `DB_PORT`      | `5432`      | PostgreSQL port            |
| `DB_USER`      | `admin`     | Database username          |
| `DB_PASSWORD`  | `admin`     | Database password          |
| `DB_DBNAME`    | `db_user`   | Database name              |

### Server
| Variable | Default | Description        |
|----------|---------|--------------------|
| `PORT`   | `8080`  | Application port   |

### Security
| Variable                   | Default            | Description                              |
|----------------------------|--------------------|------------------------------------------|
| `SECURITY_USER_ID_HEADER`  | `UserId-Header`    | Header name for forwarding user ID       |

### RabbitMQ
| Variable                              | Default                      | Description                        |
|---------------------------------------|------------------------------|------------------------------------|
| `SPRING_RABBITMQ_HOST`                | `localhost`                  | RabbitMQ host                      |
| `SPRING_RABBITMQ_PORT`                | `5672`                       | RabbitMQ port                      |
| `RABBITMQ_SYNC_EXCHANGE_ADDUSER_TOPIC`| `addUser-Topic-Exchange`     | Topic exchange for user creation   |
| `RABBITMQ_SYNC_EXCHANGE_DELETEUSER_TOPIC`| `deleteUser-Topic-Exchange` | Topic exchange for user deletion   |
| `RABBITMQ_SYNC_ROUTINGKEY_ADMIN`      | `addUser.admin`              | Routing key for admin add          |
| `RABBITMQ_SYNC_ROUTINGKEY_SELLER`     | `addUser.seller`             | Routing key for seller add         |
| `RABBITMQ_SYNC_ROUTINGKEY_CLIENT`     | `addUser.client`             | Routing key for client add         |
| `RABBITMQ_SYNC_ROUTINGKEY_DELETEUSER` | `deleteUser`                 | Routing key for user deletion      |
| `RABBITMQ_SYNC_UNIQUE_NAME`           | `user-microservice`          | Unique name for queue naming       |

---

## Build & Run

1. **Build:**
   ```sh
   ./mvnw clean package
   ```
2. **Run:**
   ```sh
   ./mvnw spring-boot:run
   ```
   Or run the generated jar:
   ```sh
   java -jar target/UserMicroservice-0.0.1-SNAPSHOT.jar
   ```

---

## Testing

Run all tests (unit and integration):
```sh
./mvnw test
```

---

## Project Structure

```
src/main/java/org/example/usermicroservice/
├── controller/         # REST controllers
├── dtos/               # Data Transfer Objects
├── entities/           # JPA entities
├── messaging/          # RabbitMQ producers/consumers
├── repositories/       # Spring Data JPA repositories
├── services/           # Business logic
└── UserMicroserviceApplication.java
```

---

## Contribution Guidelines

- Fork and branch from `main`
- Follow Java and Spring Boot best practices
- Write tests for new features
- Open a pull request with a clear description



