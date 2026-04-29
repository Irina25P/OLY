import constants.InterestType;
import dtos.ProductRequestDto;
import dtos.ProductResponseDto;
import dtos.RegisterClientDTO;
import dtos.RegisterSellerDTO;
import factories.UserFactory;
import helpers.AuthHelper;
import helpers.ImageHelper;
import helpers.ProductHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * E2E tests for product operations
 * - Create product
 * - Update product
 * - List products
 * - Delete product
 */
public class ProductTests extends BaseTest {

    private String sellerAccessToken;

    public void setupSeller() {
        // --- 1. UPLOAD THE IMAGE ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String generatedImageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");

        // --- 2. REGISTER A SELLER ---
        RegisterSellerDTO newSeller = UserFactory.createRandomValidSeller(generatedImageName);
        System.out.println("Created seller with username: " + newSeller.getUsername());

        int registrationStatus = AuthHelper.registerSeller(newSeller);
        Assertions.assertTrue(registrationStatus >= 200 && registrationStatus <= 299,
                "Seller registration failed with status: " + registrationStatus);
        System.out.println("Seller registered successfully");

        // --- 3. LOGIN THE SELLER TO GET ACCESS TOKEN ---
        sellerAccessToken = AuthHelper.login(newSeller.getUsername(), newSeller.getPassword()).getAccessToken();
    }

    @BeforeEach
    public void setupUsers() {
        setupSeller();
    }

    @Test
    public void testCreateProduct() {
        // --- 1. CREATE PRODUCT REQUEST ---
        ProductRequestDto productRequest = new ProductRequestDto();
        productRequest.setName("Test Product - Premium Laptop");
        productRequest.setDescription("High-performance laptop for developers");
        productRequest.setPrice(1299.99);
        productRequest.setStock(50);
        productRequest.setImageNames(Arrays.asList("laptop1.jpg", "laptop2.jpg"));
        productRequest.setCategory(InterestType.ELECTRONICS_IT);

        // --- 2. CREATE PRODUCT ---
        ProductResponseDto createdProduct = ProductHelper.createProduct(sellerAccessToken, productRequest);

        // --- 3. VERIFY PRODUCT ---
        Assertions.assertNotNull(createdProduct.getId(), "Product ID should not be null");
        Assertions.assertEquals(productRequest.getName(), createdProduct.getName(), "Product name mismatch");
        Assertions.assertEquals(productRequest.getDescription(), createdProduct.getDescription(), "Product description mismatch");
        Assertions.assertEquals(productRequest.getPrice(), createdProduct.getPrice(), "Product price mismatch");
        Assertions.assertEquals(productRequest.getStock(), createdProduct.getStock(), "Product stock mismatch");
        Assertions.assertEquals(productRequest.getCategory(), createdProduct.getCategory(), "Product category mismatch");
        System.out.println("Product created successfully with ID: " + createdProduct.getId());
    }

    @Test
    public void testUpdateProduct() {
        // --- 1. CREATE INITIAL PRODUCT ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        String imageName2 = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        ProductRequestDto initialProduct = new ProductRequestDto();
        initialProduct.setName("Original Product Name");
        initialProduct.setDescription("Original description");
        initialProduct.setPrice(99.99);
        initialProduct.setStock(100);
        initialProduct.setImageNames(Arrays.asList(imageName, imageName2));
        initialProduct.setCategory(InterestType.HOME_GARDEN);

        ProductResponseDto createdProduct = ProductHelper.createProduct(sellerAccessToken, initialProduct);
        UUID productId = createdProduct.getId();
        System.out.println("Created product with ID: " + productId);

        // --- 2. UPDATE PRODUCT ---
        ProductRequestDto updatedProduct = new ProductRequestDto();
        updatedProduct.setName("Updated Product Name");
        updatedProduct.setDescription("Updated description with more details");
        updatedProduct.setPrice(149.99);
        updatedProduct.setStock(75);
        updatedProduct.setImageNames(Arrays.asList("image1_updated.jpg", "image2_updated.jpg"));
        updatedProduct.setCategory(InterestType.HOME_GARDEN);

        ProductResponseDto result = ProductHelper.updateProduct(sellerAccessToken, updatedProduct, productId);

        // --- 3. VERIFY UPDATES ---
        Assertions.assertEquals(updatedProduct.getName(), result.getName(), "Product name not updated");
        Assertions.assertEquals(updatedProduct.getDescription(), result.getDescription(), "Product description not updated");
        Assertions.assertEquals(updatedProduct.getPrice(), result.getPrice(), "Product price not updated");
        Assertions.assertEquals(updatedProduct.getStock(), result.getStock(), "Product stock not updated");
        System.out.println("Product updated successfully");

        // --- 4. VERIFY VIA GET ---
        ProductResponseDto retrievedProduct = ProductHelper.getProduct(sellerAccessToken, productId);
        Assertions.assertEquals(updatedProduct.getName(), retrievedProduct.getName(), "Retrieved product has incorrect name");
        System.out.println("Product retrieval verified after update");
    }

    @Test
    public void testListSellerProducts() {
        // --- 1. CREATE MULTIPLE PRODUCTS ---
        ProductRequestDto product1 = new ProductRequestDto();
        product1.setName("Gaming Mouse");
        product1.setDescription("High precision gaming mouse");
        product1.setPrice(59.99);
        product1.setStock(200);
        product1.setImageNames(Arrays.asList("mouse.jpg"));
        product1.setCategory(InterestType.ELECTRONICS_IT);

        ProductRequestDto product2 = new ProductRequestDto();
        product2.setName("Mechanical Keyboard");
        product2.setDescription("RGB mechanical keyboard");
        product2.setPrice(129.99);
        product2.setStock(150);
        product2.setImageNames(Arrays.asList("keyboard.jpg"));
        product2.setCategory(InterestType.ELECTRONICS_IT);

        ProductResponseDto created1 = ProductHelper.createProduct(sellerAccessToken, product1);
        ProductResponseDto created2 = ProductHelper.createProduct(sellerAccessToken, product2);

        System.out.println("Created two products: " + created1.getId() + " and " + created2.getId());

        // --- 2. LIST ALL PRODUCTS ---
        List<ProductResponseDto> allProducts = ProductHelper.getAllProducts(sellerAccessToken, null, null, null, null, null);

        // --- 3. VERIFY PRODUCTS IN LIST ---
        Assertions.assertNotNull(allProducts, "Product list should not be null");
        Assertions.assertTrue(allProducts.size() >= 2, "Should have at least 2 products");

        boolean foundProduct1 = allProducts.stream().anyMatch(p -> p.getId().equals(created1.getId()));
        boolean foundProduct2 = allProducts.stream().anyMatch(p -> p.getId().equals(created2.getId()));

        Assertions.assertTrue(foundProduct1, "First product should be in the list");
        Assertions.assertTrue(foundProduct2, "Second product should be in the list");
        System.out.println("Successfully listed seller products, found " + allProducts.size() + " products");
    }

    @Test
    public void testDeleteProduct() {
        // --- 1. CREATE A PRODUCT ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String firstImageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        ProductRequestDto productToDelete = new ProductRequestDto();
        productToDelete.setName("Temporary Product");
        productToDelete.setDescription("This product will be deleted");
        productToDelete.setPrice(49.99);
        productToDelete.setStock(10);
        productToDelete.setImageNames(Arrays.asList(firstImageName));
        productToDelete.setCategory(InterestType.SPORTS_OUTDOOR);

        ProductResponseDto createdProduct = ProductHelper.createProduct(sellerAccessToken, productToDelete);
        UUID productId = createdProduct.getId();
        System.out.println("Created product for deletion: " + productId);

        // --- 2. DELETE PRODUCT ---
        int deleteStatus = ProductHelper.deleteProduct(sellerAccessToken, productId);
        Assertions.assertTrue(deleteStatus >= 200 && deleteStatus <= 299, 
                "Product deletion should return success status, got: " + deleteStatus);
        System.out.println("Product deleted with status: " + deleteStatus);
    }

    @Test
    public void testClientTryToCreateProduct() {
        // --- 1. CREATE AND REGISTER A CLIENT ---
        RegisterClientDTO newClient = UserFactory.createRandomValidClient();
        int registrationStatus = AuthHelper.registerClient(newClient);
        Assertions.assertTrue(registrationStatus >= 200 && registrationStatus <= 299,
                "Client registration failed with status: " + registrationStatus);

        // --- 2. LOGIN CLIENT TO GET TOKEN ---
        String clientAccessToken = AuthHelper.login(newClient.getUsername(), newClient.getPassword()).getAccessToken();
        System.out.println("Client logged in successfully");

        // --- 3. TRY TO CREATE PRODUCT AS CLIENT (should fail) ---
        ProductRequestDto productRequest = new ProductRequestDto();
        productRequest.setName("Unauthorized Product");
        productRequest.setDescription("Client trying to create product");
        productRequest.setPrice(99.99);
        productRequest.setStock(10);
        productRequest.setImageNames(Arrays.asList("image.jpg"));
        productRequest.setCategory(InterestType.FASHION_ACCESSORIES);

        try {
            ProductHelper.createProduct(clientAccessToken, productRequest);
            Assertions.fail("Client should not be able to create a product");
        } catch (Exception e) {
            System.out.println("Client correctly denied from creating product: " + e.getMessage());
        }
    }

    @Test
    public void testAnotherUserTryToDeleteProduct() throws InterruptedException {
        // --- 1. CREATE FIRST SELLER AND PRODUCT ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String firstImageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");

        RegisterSellerDTO seller1 = UserFactory.createRandomValidSeller(firstImageName);
        int reg1Status = AuthHelper.registerSeller(seller1);
        Assertions.assertTrue(reg1Status >= 200 && reg1Status <= 299, "Seller 1 registration failed");

        String seller1Token = AuthHelper.login(seller1.getUsername(), seller1.getPassword()).getAccessToken();
        System.out.println("Seller 1 registered and logged in");

        // --- 2. CREATE PRODUCT WITH SELLER 1 ---
        ProductRequestDto productRequest = new ProductRequestDto();
        productRequest.setName("Exclusive Seller 1 Product");
        productRequest.setDescription("Only seller 1 can delete this");
        productRequest.setPrice(199.99);
        productRequest.setStock(50);
        productRequest.setImageNames(Arrays.asList("product.jpg"));
        productRequest.setCategory(InterestType.AUTO_MOTO);

        ProductResponseDto createdProduct = ProductHelper.createProduct(seller1Token, productRequest);
        UUID productId = createdProduct.getId();
        System.out.println("Seller 1 created product: " + productId);

        // --- 3. CREATE AND REGISTER SECOND SELLER ---
        String secondImageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        RegisterSellerDTO seller2 = UserFactory.createRandomValidSeller(secondImageName);
        int reg2Status = AuthHelper.registerSeller(seller2);
        Assertions.assertTrue(reg2Status >= 200 && reg2Status <= 299, "Seller 2 registration failed");

        String seller2Token = AuthHelper.login(seller2.getUsername(), seller2.getPassword()).getAccessToken();
        System.out.println("Seller 2 registered and logged in");

        // --- 4. SELLER 2 TRIES TO DELETE SELLER 1'S PRODUCT (should fail) ---
        int statusCode = ProductHelper.deleteProduct(seller2Token, productId);
        Assertions.assertTrue(statusCode >= 400 && statusCode <= 499, "Seller 2 registration failed");

        // --- 5. VERIFY SELLER 1 CAN STILL DELETE THEIR OWN PRODUCT ---
        int deleteStatus = ProductHelper.deleteProduct(seller1Token, productId);
        Assertions.assertTrue(deleteStatus >= 200 && deleteStatus <= 299,
                "Seller 1 should be able to delete their own product");
        System.out.println("Seller 1 successfully deleted their own product");
    }
    
    @Test 
    public void queryProducts() {
        // --- 1. CREATE PRODUCTS WITH DIFFERENT CATEGORIES AND PRICES ---
        ProductRequestDto product1 = new ProductRequestDto();
        product1.setName("Laptop Computer");
        product1.setDescription("Powerful laptop");
        product1.setPrice(1200.0);
        product1.setStock(10);
        product1.setImageNames(Arrays.asList("laptop.jpg"));
        product1.setCategory(InterestType.ELECTRONICS_IT);

        ProductRequestDto product2 = new ProductRequestDto();
        product2.setName("Tennis Racket");
        product2.setDescription("Sports equipment");
        product2.setPrice(89.99);
        product2.setStock(30);
        product2.setImageNames(Arrays.asList("racket.jpg"));
        product2.setCategory(InterestType.SPORTS_OUTDOOR);

        ProductRequestDto product3 = new ProductRequestDto();
        product3.setName("Garden Chair");
        product3.setDescription("Outdoor furniture");
        product3.setPrice(149.99);
        product3.setStock(50);
        product3.setImageNames(Arrays.asList("chair.jpg"));
        product3.setCategory(InterestType.HOME_GARDEN);

        ProductHelper.createProduct(sellerAccessToken, product1);
        ProductHelper.createProduct(sellerAccessToken, product2);
        ProductHelper.createProduct(sellerAccessToken, product3);
        System.out.println("Created 3 products with different categories and prices");

        // --- 2. QUERY BY CATEGORY ---
        List<ProductResponseDto> electronicsProducts = ProductHelper.getAllProducts(
                sellerAccessToken,
                null,
                Arrays.asList(InterestType.ELECTRONICS_IT),
                null,
                null,
                null
        );
        Assertions.assertTrue(electronicsProducts.stream().anyMatch(p -> p.getName().equals("Laptop Computer")),
                "Should find electronics product");
        System.out.println("Query by category (ELECTRONICS_IT) returned " + electronicsProducts.size() + " products");

        // --- 3. QUERY BY PRICE RANGE ---
        List<ProductResponseDto> priceRangeProducts = ProductHelper.getAllProducts(
                sellerAccessToken,
                null,
                null,
                80.0,
                200.0,
                null
        );
        Assertions.assertTrue(priceRangeProducts.stream().anyMatch(p -> p.getName().equals("Tennis Racket")),
                "Should find product in price range");
        Assertions.assertTrue(priceRangeProducts.stream().anyMatch(p -> p.getName().equals("Garden Chair")),
                "Should find product in price range");
        System.out.println("Query by price range (80-200) returned " + priceRangeProducts.size() + " products");

        // --- 4. QUERY BY MINIMUM PRICE ---
        List<ProductResponseDto> expensiveProducts = ProductHelper.getAllProducts(
                sellerAccessToken,
                null,
                null,
                1000.0,
                null,
                null
        );
        Assertions.assertTrue(expensiveProducts.stream().anyMatch(p -> p.getName().equals("Laptop Computer")),
                "Should find expensive product");
        System.out.println("Query by min price (1000) returned " + expensiveProducts.size() + " products");

        // --- 5. QUERY BY MAXIMUM PRICE ---
        List<ProductResponseDto> cheapProducts = ProductHelper.getAllProducts(
                sellerAccessToken,
                null,
                null,
                null,
                100.0,
                null
        );
        Assertions.assertTrue(cheapProducts.stream().anyMatch(p -> p.getName().equals("Tennis Racket")),
                "Should find cheap product");
        System.out.println("Query by max price (100) returned " + cheapProducts.size() + " products");

        // --- 6. QUERY BY NAME ---
        List<ProductResponseDto> laptopProducts = ProductHelper.getAllProducts(
                sellerAccessToken,
                "Laptop",
                null,
                null,
                null,
                null
        );
        Assertions.assertTrue(laptopProducts.stream().anyMatch(p -> p.getName().equals("Laptop Computer")),
                "Should find product by name");
        System.out.println("Query by name (Laptop) returned " + laptopProducts.size() + " products");

        // --- 7. QUERY WITH MULTIPLE CATEGORIES ---
        List<ProductResponseDto> multiCategoryProducts = ProductHelper.getAllProducts(
                sellerAccessToken,
                null,
                Arrays.asList(InterestType.SPORTS_OUTDOOR, InterestType.HOME_GARDEN),
                null,
                null,
                null
        );
        Assertions.assertTrue(multiCategoryProducts.stream().anyMatch(p -> p.getName().equals("Tennis Racket")),
                "Should find sports product");
        Assertions.assertTrue(multiCategoryProducts.stream().anyMatch(p -> p.getName().equals("Garden Chair")),
                "Should find garden product");
        System.out.println("Query by multiple categories returned " + multiCategoryProducts.size() + " products");
    }

}

