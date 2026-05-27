# Dynamic Pricing Microservice

## Overview
The Dynamic Pricing Microservice computes price recommendations per product for sellers.
It receives product and order events, tracks demand, and proposes a direction:
- `INCREASE`
- `DECREASE`
- `MAINTAIN`

Price changes are not auto-applied by default. A seller must call the apply endpoint.

## Responsibilities
- Maintain product demand state (`product_demand` table).
- Track order demand records (`order_demand_records` table).
- Expose recommendation endpoints for sellers.
- Publish a price-update event when a recommendation is applied.

## REST API
Base path: `/v1/pricing`

### GET /
- Description: Returns all recommendations for authenticated seller.
- Security: `Role-Header: SELLER`
- Required headers:
  - `UserId-Header: <seller-uuid>`
  - `Role-Header: SELLER`

Example:

```bash
curl -s \
  -H 'UserId-Header: <seller-uuid>' \
  -H 'Role-Header: SELLER' \
  http://localhost:8088/v1/pricing
```

### GET /{productId}
- Description: Returns recommendation details for one product.
- Security: `Role-Header: SELLER`

Example:

```bash
curl -s \
  -H 'UserId-Header: <seller-uuid>' \
  -H 'Role-Header: SELLER' \
  http://localhost:8088/v1/pricing/<product-uuid>
```

### POST /{productId}/apply
- Description: Applies recommended price and emits RabbitMQ price-update event.
- Security: `Role-Header: SELLER`

Example:

```bash
curl -s -X POST \
  -H 'UserId-Header: <seller-uuid>' \
  -H 'Role-Header: SELLER' \
  http://localhost:8088/v1/pricing/<product-uuid>/apply
```

## Recommendation Logic
Current rule set:

- `INCREASE`
  - Condition: `count7Days > weeklyAvg * 1.5` and `stock < 20`
  - Factor: up to +20%

- `DECREASE`
  - Condition: `count30Days > 0` and `count7Days < weeklyAvg * 0.5` and `stock > 50`
  - Factor: -10%

- `MAINTAIN`
  - Otherwise

Where:
- `count7Days` = orders in last 7 days
- `count30Days` = orders in last 30 days
- `weeklyAvg = count30Days / 4.0`

## RabbitMQ Integration
Consumes:
- `order-event-Topic-Exchange` + `orderEvent`
  - Builds demand records from order items.

- `product-Topic-Exchange` + `addProduct`
- `product-Topic-Exchange` + `updateProduct`
- `product-Topic-Exchange` + `deleteProduct`
  - Keeps product demand data synchronized with product service.

Publishes:
- `price-update-Topic-Exchange` + `priceUpdate`
  - Sent only on `/v1/pricing/{productId}/apply`.

Queue naming pattern:
- `<exchange>.<RABBITMQ_SYNC_UNIQUE_NAME>.<routingKey>`
- Default unique name in compose: `dynamic-pricing-service`

The consumer accepts both plain JSON payloads and quoted JSON payloads.

## Configuration
Main application properties are in `src/main/resources/application.properties`.

Important environment variables:
- `DB_IP`, `DB_PORT`, `DB_USER`, `DB_PASSWORD`, `DB_DBNAME`
- `SPRING_RABBITMQ_HOST`, `SPRING_RABBITMQ_PORT`
- `RABBITMQ_SYNC_UNIQUE_NAME`
- `SECURITY_USER_ID_HEADER`, `SECURITY_ROLE_HEADER`

## Local Run
With docker compose (recommended from repository root):

```bash
docker compose up -d dynamic-pricing-service rabbitmq
```

Service port in compose:
- `8088 -> 8080` (container)

## Verification Quick Check
1. Place orders for a product.
2. Query recommendation with seller headers.
3. If direction is `INCREASE` or `DECREASE`, call apply endpoint.
4. Confirm product price changed in Product service.
