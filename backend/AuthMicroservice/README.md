# AuthMicroservice

A Spring Boot authentication microservice that handles user registration, login, JWT token generation/verification, and user synchronization via RabbitMQ topic exchanges.

## Endpoints

### Registration

| Method | Path                        | Description            | Request Body          | Response                |
|--------|-----------------------------|------------------------|-----------------------|-------------------------|
| POST   | `/v1/auth/register/admin`   | Register an admin user | `RegisterAdminDTO`    | `201 Created` / `400`   |
| POST   | `/v1/auth/register/seller`  | Register a seller user | `RegisterSellerDTO`   | `201 Created` / `400`   |
| POST   | `/v1/auth/register/client`  | Register a client user | `RegisterClientDTO`   | `201 Created` / `400`   |

#### Request Body Examples

**Register Admin:**
```json
{
  "username": "admin1",
  "password": "securePass123"
}
```

**Register Seller:**
```json
{
  "name": "My Shop",
  "email": "seller@example.com",
  "username": "seller1",
  "password": "securePass123",
  "logoURL": "https://example.com/logo.png",
  "description": "Best shop in town",
  "region": "EU"
}
```

**Register Client:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "username": "johndoe",
  "password": "securePass123",
  "region": "EU",
  "age": 25,
  "phoneNumber": "+40712345678",
  "interests": ["electronics", "books"]
}
```

### Authentication

| Method | Path              | Description                        | Request Body   | Response                          |
|--------|-------------------|------------------------------------|----------------|-----------------------------------|
| POST   | `/v1/auth/login`  | Authenticate and receive JWT token | `AuthRequest`  | `200 OK` with `AuthResponse`     |
| GET    | `/v1/auth/verify` | Verify a JWT token                 | —              | `200 OK` with headers / `401`    |

#### Login Request
```json
{
  "username": "johndoe",
  "password": "securePass123"
}
```

#### Login Response
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### Verify Token
Send the JWT token in the `Authorization` header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

On success, the response includes the following headers:

| Header             | Description                                   |
|--------------------|-----------------------------------------------|
| `Username-Header`  | The authenticated user's username             |
| `Role-Header`      | The user's role (`ADMIN`, `SELLER`, `CLIENT`) |
| `UserId-Header`    | The user's UUID                               |

> Header names are configurable via environment variables.

---

## RabbitMQ

### Producer (sends messages)

On user registration, a message is published to the **addUser Topic Exchange** with the appropriate routing key:

| Routing Key     | Message DTO         | Description                  |
|-----------------|---------------------|------------------------------|
| `addUser.admin` | `AdminMessageDTO`   | Admin registered (id, username) |
| `addUser.seller`| `SellerMessageDTO`  | Seller registered (id, name, email, username, logoURL, description, region) |
| `addUser.client`| `ClientMessageDTO`  | Client registered (id, firstName, lastName, email, username, region, age, phoneNumber, interests) |

> **Note:** Password is never included in RabbitMQ messages.

### Consumer (receives messages)

| Queue                                           | Routing Key    | Description                          |
|-------------------------------------------------|----------------|--------------------------------------|
| `deleteUser-Topic-Exchange.auth-microservice`   | `deleteUser`   | Deletes user by UUID from auth DB    |

Expected message format:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

## Environment Variables

### Database
| Variable       | Default     | Description                |
|----------------|-------------|----------------------------|
| `DB_IP`        | `localhost` | PostgreSQL host IP         |
| `DB_PORT`      | `5432`      | PostgreSQL port            |
| `DB_USER`      | `admin`     | Database username          |
| `DB_PASSWORD`  | `admin`     | Database password          |
| `DB_DBNAME`    | `db_auth`   | Database name              |

### Server
| Variable | Default | Description        |
|----------|---------|--------------------|
| `PORT`   | `8080`  | Application port   |

### Security & JWT
| Variable                   | Default            | Description                              |
|----------------------------|--------------------|------------------------------------------|
| `JWT_SECRET_TOKEN`         | `O4/hhlvFjaIftjZ7R1/pfbN+FT6A0cp5rfRm8pZHKbU=` | JWT secret key for signing tokens |
| `SECURITY_USERNAME_HEADER` | `Username-Header`  | Header name for forwarding username      |
| `SECURITY_ROLE_HEADER`     | `Role-Header`      | Header name for forwarding role          |
| `SECURITY_USER_ID_HEADER`  | `UserId-Header`    | Header name for forwarding user ID       |

### RabbitMQ
| Variable                              | Default                      | Description                        |
|---------------------------------------|------------------------------|------------------------------------|
| `SPRING_RABBITMQ_HOST`                | `localhost`                  | RabbitMQ host                      |
| `SPRING_RABBITMQ_PORT`                | `5672`                       | RabbitMQ port                      |
| `RABBITMQ_SYNC_EXCHANGE_ADDUSER_TOPIC`| `addUser-Topic-Exchange`     | Topic exchange for user creation   |
| `RABBITMQ_SYNC_EXCHANGE_DELETEUSER_TOPIC`| `deleteUser-Topic-Exchange` | Topic exchange for user deletion   |
| `RABBITMQ_SYNC_ROUTINGKEY_ADMIN`      | `addUser.admin`              | Routing key for admin registration |
| `RABBITMQ_SYNC_ROUTINGKEY_SELLER`     | `addUser.seller`             | Routing key for seller registration|
| `RABBITMQ_SYNC_ROUTINGKEY_CLIENT`     | `addUser.client`             | Routing key for client registration|
| `RABBITMQ_SYNC_ROUTINGKEY_DELETEUSER` | `deleteUser`                 | Routing key for user deletion      |
| `RABBITMQ_SYNC_UNIQUE_NAME`           | `auth-microservice`          | Unique name for queue naming       |
