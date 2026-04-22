# OrderMicroservice

Spring Boot microservice for creating orders, listing orders by user role (client/seller), and fetching a full order (order + ordered products).

## Tech Stack
- Java 25 (from `pom.xml`)
- Spring Boot 4.0.5
- Spring Web
- Spring Data JPA
- PostgreSQL
- Spring Validation
- Spring Security (`@PreAuthorize` support)

## Dependencies
Main dependencies already present in `pom.xml`:
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-validation`
- `spring-boot-starter-security`
- `postgresql`
- `lombok`

## Configuration
From `src/main/resources/application.properties`:
- `server.port=${PORT:8080}`
- `spring.datasource.url=jdbc:postgresql://${DB_IP:localhost}:${DB_PORT:5432}/${DB_DBNAME:db_order}`
- `spring.datasource.username=${DB_USER:admin}`
- `spring.datasource.password=${DB_PASSWORD:admin}`

### Security header required by controller
The controller expects a request header name configured as:
- `${security.user-id-header}`

Add this property to your `application.properties` (example):

```properties
security.user-id-header=X-User-Id
```

Then send:
- `X-User-Id: <user-uuid>`

## Data Model Used by API

### `OrderDto`
```json
{
  "id": "uuid (optional in create)",
  "clientId": "uuid",
  "sellerId": "uuid",
  "statusType": "PLACED | SHIPPED | IN_DELIVERY | CANCELLED | RECEIVED",
  "payment": "CARD | CASH_ON_DELIVERY",
  "shippingDetails": "string",
  "fullPrice": "double (total price: sum of all products * quantities)"
}
```

### `OrderProductDto`
```json
{
  "id": "uuid (optional)",
  "orderId": "uuid (optional in create, set by backend)",
  "productId": "uuid",
  "quantity": 1,
  "pricePerProduct": "double (price of the product at time of order, set by backend)"
}
```

### `OrderFullDto`
```json
{
  "order": { "...OrderDto" },
  "orderProducts": [
    { "...OrderProductDto" }
  ]
}
```

---

## API Endpoints
Base path: `/v1/orders`

## 1) Get orders for logged-in client
- Method: `GET`
- Path: `/v1/orders/client`
- Security: `@PreAuthorize("hasRole('CLIENT')")`
- Required header: `${security.user-id-header}: <client-uuid>`

### Example request
```http
GET /v1/orders/client HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
X-User-Id: 11111111-1111-1111-1111-111111111111
```

### Example response (`200 OK`)
```json
[
  {
    "id": "3f9d4b16-08cc-4ce9-9f7f-4d77a4f6f3ad",
    "clientId": "11111111-1111-1111-1111-111111111111",
    "sellerId": "22222222-2222-2222-2222-222222222222",
    "statusType": "PLACED",
    "payment": "CARD",
    "shippingDetails": "Cluj-Napoca, Str. Memorandumului 12",
    "fullPrice": 49.98
  }
]
```

## 2) Get orders for logged-in seller
- Method: `GET`
- Path: `/v1/orders/seller`
- Security: `@PreAuthorize("hasRole('SELLER')")`
- Required header: `${security.user-id-header}: <seller-uuid>`

### Example request
```http
GET /v1/orders/seller HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
X-User-Id: 22222222-2222-2222-2222-222222222222
```

### Example response (`200 OK`)
```json
[
  {
    "id": "3f9d4b16-08cc-4ce9-9f7f-4d77a4f6f3ad",
    "clientId": "11111111-1111-1111-1111-111111111111",
    "sellerId": "22222222-2222-2222-2222-222222222222",
    "statusType": "SHIPPED",
    "payment": "CASH_ON_DELIVERY",
    "shippingDetails": "Bucharest, Str. Aviatorilor 22",
    "fullPrice": 75.50
  }
]
```

## 3) Get full order by id (order + products)
- Method: `GET`
- Path: `/v1/orders/{orderId}`
- Required header: `${security.user-id-header}: <user-uuid>`
- Access rule in service: user must be either order `client` or `seller`

### Example request
```http
GET /v1/orders/3f9d4b16-08cc-4ce9-9f7f-4d77a4f6f3ad HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
X-User-Id: 11111111-1111-1111-1111-111111111111
```

### Example response (`200 OK`)
```json
{
  "order": {
    "id": "3f9d4b16-08cc-4ce9-9f7f-4d77a4f6f3ad",
    "clientId": "11111111-1111-1111-1111-111111111111",
    "sellerId": "22222222-2222-2222-2222-222222222222",
    "statusType": "PLACED",
    "payment": "CARD",
    "shippingDetails": "Cluj-Napoca, Str. Memorandumului 12",
    "fullPrice": 49.98
  },
  "orderProducts": [
    {
      "id": "55ec539f-73d8-46c0-93ad-06973316c8b1",
      "orderId": "3f9d4b16-08cc-4ce9-9f7f-4d77a4f6f3ad",
      "productId": "31ebec9e-8e5d-44f5-8fc0-4b43fe6b2ca0",
      "quantity": 2,
      "pricePerProduct": 24.99
    }
  ]
}
```

### Typical error cases
- User not found -> runtime exception (without a global exception handler this may return `500`)
- Order not found -> runtime exception (without handler may return `500`)
- User is not owner (not client/seller) -> `AccessDeniedException` (commonly mapped to `403`)

## 4) Create order
- Method: `POST`
- Path: `/v1/orders`
- Request body: `OrderFullDto`
- Validation: `@Valid` + `@NotNull` on DTO fields
- Current response type: plain `String`

### Example request
```http
POST /v1/orders HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer <token>

{
  "order": {
    "clientId": "11111111-1111-1111-1111-111111111111",
    "sellerId": "22222222-2222-2222-2222-222222222222",
    "statusType": "PLACED",
    "payment": "CARD",
    "shippingDetails": "Cluj-Napoca, Str. Memorandumului 12"
  },
  "orderProducts": [
    {
      "productId": "31ebec9e-8e5d-44f5-8fc0-4b43fe6b2ca0",
      "quantity": 2
    },
    {
      "productId": "4fbce7f9-a73b-4bba-95e5-9ce78df3fbe9",
      "quantity": 1
    }
  ]
}
```

### Example response (`201 Created`)
```text
Your order has been placed successfully!
```

---

## Quick Run
If your Java/Maven environment is set correctly:

```powershell
cd "C:\Users\flavi\OneDrive\Documents\DISI\30644_Stan_Mihai_Alexandru_OnlineShop\backend\OrderMicroservice"
.\mvnw.cmd spring-boot:run
```

## Notes
- Method security annotations (`@PreAuthorize`) also require method security to be enabled in your security config (for example with `@EnableMethodSecurity`).
- DTO field in code is named `ShippingDetails` (capital `S`), but JSON examples use `shippingDetails`.
- For cleaner API behavior, consider adding a global exception handler (`@ControllerAdvice`) to return structured `404/403/400` responses.
- **Price Calculation**: 
  - `pricePerProduct` in `OrderProductDto` is automatically populated from the `Product` entity's price when fetching orders.
  - `fullPrice` in `OrderDto` is calculated by summing all `(pricePerProduct × quantity)` for all products in the order.
  - Prices are computed by the service layer and are read-only from the API perspective (set by backend).

