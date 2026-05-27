# Recommendation Microservice

## Overview
The Recommendation Microservice provides personalized product recommendations for clients.
It combines three strategies:

1. Collaborative filtering based on products purchased by similar users.
2. Category fallback based on categories from the user's purchase history.
3. Popularity fallback based on globally most purchased products.

It stores graph data in Neo4j:
- Nodes: `User`, `Product`
- Relationships: `PURCHASED`, `VIEWED`

## Responsibilities
- Serve recommendations to authenticated clients.
- Track product views to improve future recommendations.
- Consume product and order events from RabbitMQ to keep the graph in sync.

## REST API
Base path: `/v1/recommendations`

### GET /
- Description: Returns recommendations for the authenticated client.
- Security: `Role-Header: CLIENT`
- Required headers:
  - `UserId-Header: <client-uuid>`
  - `Role-Header: CLIENT`

Example:

```bash
curl -s \
  -H 'UserId-Header: <client-uuid>' \
  -H 'Role-Header: CLIENT' \
  http://localhost:8087/v1/recommendations
```

### POST /view
- Description: Records a product view event for the authenticated client.
- Security: `Role-Header: CLIENT`
- Required headers:
  - `UserId-Header: <client-uuid>`
  - `Role-Header: CLIENT`
- Body:

```json
{
  "productId": "017c1351-d51e-4674-9408-14b87223500c"
}
```

Example:

```bash
curl -s -X POST \
  -H 'Content-Type: application/json' \
  -H 'UserId-Header: <client-uuid>' \
  -H 'Role-Header: CLIENT' \
  -d '{"productId":"017c1351-d51e-4674-9408-14b87223500c"}' \
  http://localhost:8087/v1/recommendations/view
```

## RabbitMQ Integration
Consumes these topics/events:

- `order-event-Topic-Exchange` + `orderEvent`
  - Consumed as order event payload.
  - Used to create `PURCHASED` relationships.

- `product-Topic-Exchange` + `addProduct`
- `product-Topic-Exchange` + `updateProduct`
  - Upserts product node metadata.

- `product-Topic-Exchange` + `deleteProduct`
  - Deletes product node.

Queue naming pattern:
- `<exchange>.<RABBITMQ_SYNC_UNIQUE_NAME>.<routingKey>`
- Default unique name in compose: `recommendation-service`

The consumer accepts both plain JSON payloads and quoted JSON payloads.

## Configuration
Main application properties are in `src/main/resources/application.properties`.

Important environment variables:
- `NEO4J_URI`
- `NEO4J_USER`
- `NEO4J_PASSWORD`
- `SPRING_RABBITMQ_HOST`
- `SPRING_RABBITMQ_PORT`
- `RABBITMQ_SYNC_UNIQUE_NAME`
- `SECURITY_USER_ID_HEADER`
- `SECURITY_ROLE_HEADER`

## Local Run
With docker compose (recommended from repository root):

```bash
docker compose up -d recommendation-service neo4j rabbitmq
```

Service port in compose:
- `8087 -> 8080` (container)

## Notes
- Initial Neo4j warnings about missing labels/relationship types are expected on an empty graph.
- Recommendations improve as product and order events are consumed over time.
