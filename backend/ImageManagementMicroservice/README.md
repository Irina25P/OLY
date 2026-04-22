# ImageManagementMicroservice

A Spring Boot microservice for managing image uploads and deletions with MinIO object storage integration. Provides secure endpoints for image operations with role-based access control and RabbitMQ integration for event synchronization.

## Endpoints

### Image Management

| Method | Path            | Description              | Authorization      | Request Body | Response                |
|--------|-----------------|--------------------------|-------------------|--------------|-------------------------|
| POST   | `/v1/images`    | Upload an image file     | CLIENT, SELLER    | Multipart    | `200 OK` / `400` / `403` |
| DELETE | `/v1/images`    | Delete an image by name  | CLIENT, SELLER    | Query Param  | `200 OK` / `400` / `403` |

---

## Upload Image

### Request

**Method:** `POST /v1/images`

**Authorization:** Requires `CLIENT` or `SELLER` role

**Required Headers:**
```
Username-Header: <username>
Role-Header: <CLIENT|SELLER>
```

**Content-Type:** `multipart/form-data`

**Form Parameters:**
- `file` (required): The image file to upload
  - Supported formats: PNG, JPEG, JPG, GIF, WEBP, SVG
  - Must have valid image content-type header

**Example using cURL:**
```bash
curl -X POST http://localhost:8080/v1/images \
  -H "Username-Header: johndoe" \
  -H "Role-Header: CLIENT" \
  -F "file=@/path/to/image.png"
```

**Example using JavaScript/Fetch:**
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);

fetch('http://localhost:8080/v1/images', {
  method: 'POST',
  headers: {
    'Username-Header': 'johndoe',
    'Role-Header': 'CLIENT'
  },
  body: formData
})
.then(response => response.json())
.then(data => console.log(data));
```

### Response

**Success (200 OK):**
```json
{
  "success": true,
  "message": "Image uploaded successfully",
  "objectName": "550e8400-e29b-41d4-a716-446655440000.png",
  "fileName": "my-image.png"
}
```

**Error - Empty File (400 Bad Request):**
```json
{
  "success": false,
  "message": "File is empty"
}
```

**Error - Invalid File Type (400 Bad Request):**
```json
{
  "success": false,
  "message": "File is empty"
}
```

**Error - Unauthorized (403 Forbidden):**
```json
{
  "success": false,
  "message": "Access Denied"
}
```

**Error - Internal Server Error (500):**
```json
{
  "success": false,
  "message": "Error uploading image: <error details>"
}
```

---

## Delete Image

### Request

**Method:** `DELETE /v1/images`

**Authorization:** Requires `CLIENT` or `SELLER` role

**Required Headers:**
```
Username-Header: <username>
Role-Header: <CLIENT|SELLER>
```

**Query Parameters:**
- `objectName` (required): The MinIO object name returned from upload

**Example using cURL:**
```bash
curl -X DELETE "http://localhost:8080/v1/images?objectName=550e8400-e29b-41d4-a716-446655440000.png" \
  -H "Username-Header: johndoe" \
  -H "Role-Header: CLIENT"
```

**Example using JavaScript/Fetch:**
```javascript
fetch(`http://localhost:8080/v1/images?objectName=${objectName}`, {
  method: 'DELETE',
  headers: {
    'Username-Header': 'johndoe',
    'Role-Header': 'CLIENT'
  }
})
.then(response => response.json())
.then(data => console.log(data));
```

### Response

**Success (200 OK):**
```json
{
  "success": true,
  "message": "Image deleted successfully"
}
```

**Error - Empty Object Name (400 Bad Request):**
```json
{
  "success": false,
  "message": "Object name is required"
}
```

**Error - Unauthorized (403 Forbidden):**
```json
{
  "success": false,
  "message": "Access Denied"
}
```

**Error - Internal Server Error (500):**
```json
{
  "success": false,
  "message": "Error deleting image: <error details>"
}
```

---

## MinIO Storage

### Configuration

Images are stored in MinIO object storage with the following structure:

- **Bucket Name:** Configurable via `minio.bucketName`
- **Object Naming:** `<UUID>.<file-extension>`
  - Example: `550e8400-e29b-41d4-a716-446655440000.png`
  - Files without extension: `550e8400-e29b-41d4-a716-446655440000`

### Supported Image Formats

| Format | MIME Type        | Extension |
|--------|------------------|-----------|
| PNG    | `image/png`      | `.png`    |
| JPEG   | `image/jpeg`     | `.jpg`, `.jpeg` |
| GIF    | `image/gif`      | `.gif`    |
| WEBP   | `image/webp`     | `.webp`   |
| SVG    | `image/svg+xml`  | `.svg`    |

---

## RabbitMQ Integration

### Message Events

The microservice publishes events to RabbitMQ for synchronization with other services.

**Note:** RabbitMQ integration is available for future event-driven features. Current implementation publishes image-related events for potential subscribers.

---

## Security & Authorization

### Role-Based Access Control

- **CLIENT Role:** Can upload and delete images
- **SELLER Role:** Can upload and delete images
- **ADMIN Role:** Cannot access image endpoints (403 Forbidden)

### Authentication Headers

The microservice validates the following headers from upstream API Gateway:

| Header              | Description                   | Example Value |
|---------------------|-------------------------------|---------------|
| `Username-Header`   | Authenticated username        | `johndoe`     |
| `Role-Header`       | User's role                   | `CLIENT`      |
| `UserId-Header`     | User's UUID (optional)        | `550e8400-...` |

> **Header names are configurable via environment variables.**

---

## Environment Variables

### MinIO Configuration

| Variable            | Default          | Description                    |
|---------------------|------------------|--------------------------------|
| `MINIO_IP`          | `92.5.49.167`    | MinIO server IP address        |
| `MINIO_PORT`        | `9000`           | MinIO server port              |
| `MINIO_ACCESS_KEY`  | `admin`          | MinIO access key               |
| `MINIO_SECRET_KEY`  | `admin`          | MinIO secret key               |
| `MINIO_BUCKET_NAME` | `disi-images`    | MinIO bucket name              |

### Server Configuration

| Variable | Default | Description        |
|----------|---------|--------------------|
| `PORT`   | `8080`  | Application port   |

### Security & JWT

| Variable                   | Default            | Description                        |
|----------------------------|--------------------|-------------------------------------|
| `SECURITY_USERNAME_HEADER` | `Username-Header`  | Header name for username           |
| `SECURITY_ROLE_HEADER`     | `Role-Header`      | Header name for role               |
| `SECURITY_USER_ID_HEADER`  | `UserId-Header`    | Header name for user ID            |
| `JWT_SECRET`               | `O4/hhlvFjaIftjZ7R1/pfbN+FT6A0cp5rfRm8pZHKbU=` | JWT secret for validation |

### RabbitMQ Configuration

| Variable                              | Default                      | Description                         |
|---------------------------------------|------------------------------|-------------------------------------|
| `SPRING_RABBITMQ_HOST`                | `localhost`                  | RabbitMQ host                       |
| `SPRING_RABBITMQ_PORT`                | `5672`                       | RabbitMQ port                       |
| `RABBITMQ_SYNC_EXCHANGE_ADDUSER_TOPIC`| `addUser-Topic-Exchange`     | Topic exchange for add user events  |
| `RABBITMQ_SYNC_EXCHANGE_DELETEUSER_TOPIC`| `deleteUser-Topic-Exchange` | Topic exchange for delete user events |
| `RABBITMQ_SYNC_ROUTINGKEY_ADMIN`      | `addUser.admin`              | Routing key for admin events        |
| `RABBITMQ_SYNC_ROUTINGKEY_SELLER`     | `addUser.seller`             | Routing key for seller events       |
| `RABBITMQ_SYNC_ROUTINGKEY_CLIENT`     | `addUser.client`             | Routing key for client events       |
| `RABBITMQ_SYNC_ROUTINGKEY_DELETEUSER` | `deleteUser`                 | Routing key for user deletion       |
| `RABBITMQ_SYNC_UNIQUE_NAME`           | `auth-microservice`          | Unique name for queue naming        |


## Error Handling

The microservice provides clear error messages and HTTP status codes:

| Status | Meaning          | Example Scenario                           |
|--------|------------------|--------------------------------------------|
| 200    | OK               | Image uploaded/deleted successfully        |
| 400    | Bad Request      | Empty file, invalid format, missing params |
| 403    | Forbidden        | Insufficient role, missing headers         |
| 500    | Server Error     | MinIO connection failure, file system error|





