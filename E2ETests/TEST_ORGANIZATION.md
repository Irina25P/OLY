# E2E Test Suite Organization

This E2E test suite is organized for maintainability and extensibility. Here's the structure:

## Directory Structure

### Test Classes
- **UserRegistrationTests.java** - Tests for user registration flows (Seller & Client)
- **ProductTests.java** - Tests for product operations (PLACEHOLDER - to be implemented)
- **OrderTests.java** - Tests for order operations (PLACEHOLDER - to be implemented)
- **ReviewTests.java** - Tests for review operations (PLACEHOLDER - to be implemented)
- **AddUserTests.java** - Legacy test file (can be kept for reference or removed)

### Helper Classes (in `helpers/` package)
- **AuthHelper.java** - Authentication operations (register, login)
- **UserProfileHelper.java** - User profile operations (get profile, delete profile)
- **ImageHelper.java** - Image upload operations

### Constants
- **ApiEndpoints.java** - All API endpoint paths
- **County.java** - Romanian county enum
- **InterestType.java** - Interest type enum

### Utilities
- **BaseTest.java** - Base test class with common setup and utilities

### DTOs (in `dtos/` package)
- **AuthRequest/AuthResponse** - Authentication DTOs
- **RegisterSellerDTO/RegisterClientDTO** - Registration DTOs
- **SellerDTO/ClientDTO** - User profile DTOs

### Factories (in `factories/` package)
- **UserFactory.java** - Creates random valid test users

## Running Tests

### Run all tests
```bash
mvn test
```

### Run specific test class
```bash
mvn test -Dtest=UserRegistrationTests
```

### Run specific test method
```bash
mvn test -Dtest=UserRegistrationTests#testClientRegistrationFlow
```

### Run with more verbose output
```bash
mvn test -X
```

## How to Add New Tests

### 1. Create a new test class extending BaseTest
```java
public class MyFeatureTests extends BaseTest {
    
    @Test
    public void testMyFeature() {
        // Your test here
    }
}
```

### 2. Use Helper Classes for Common Operations

Instead of writing REST API calls directly, use the helper classes:

```java
// Authentication
AuthResponse response = AuthHelper.login(username, password);
int statusCode = AuthHelper.registerSeller(sellerDTO);

// User Profile
ClientDTO profile = UserProfileHelper.getClientProfile(accessToken);
int deleteStatus = UserProfileHelper.deleteClientProfile(accessToken);

// Images
String imageName = ImageHelper.uploadImage(file, "image/jpeg");
```

### 3. Use ApiEndpoints Constants

```java
// Instead of hardcoding: "/auth/login"
// Use the constant:
given().post(ApiEndpoints.AUTH_LOGIN)
```

### 4. Use BaseTest Utilities

```java
// Wait for async operations
waitForAsync();           // 2 seconds
waitForAsync(5000);       // 5 seconds
```

## Adding New Helper Classes

When you need to add helpers for new features:

1. Create a new file in `helpers/` package
2. Follow the same pattern as existing helpers
3. Return either data objects or status codes
4. Document with JavaDoc comments

Example:
```java
package helpers;

public class ProductHelper {
    
    /**
     * Creates a new product
     */
    public static String createProduct(ProductDTO product, String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post(ApiEndpoints.PRODUCTS)
                .then()
                .extract()
                .path("id");
    }
}
```

## Test Data

Tests use the `UserFactory` class to generate random but valid test data. This ensures:
- No conflicts between test runs
- Realistic test scenarios
- Easy to extend for new user types

## Best Practices

1. **Use meaningful test names** - Describe what is being tested
2. **Use helper methods** - Don't repeat REST calls
3. **Use constants** - Don't hardcode endpoints
4. **Clean up after tests** - Delete created resources
5. **Add logging** - Use System.out.println() for important milestones
6. **Verify assertions** - Don't just check status codes
7. **Document tests** - Add JavaDoc comments explaining complex scenarios

