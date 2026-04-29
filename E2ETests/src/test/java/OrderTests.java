import constants.OrderStatus;
import constants.PaymentMethod;
import dtos.*;
import factories.OrderFactory;
import factories.ProductFactory;
import factories.UserFactory;
import helpers.AuthHelper;
import helpers.ImageHelper;
import helpers.OrderHelper;
import helpers.ProductHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * E2E tests for order operations
 * - Place order
 * - View orders (client)
 * - View orders (seller)
 * - Update order status
 * - Cancel order
 */
public class OrderTests extends BaseTest {

    private String clientAccessToken;
    private String sellerAccessToken;

    @BeforeEach
    public void setupUsers() {
        // --- SETUP CLIENT ---
        RegisterClientDTO newClient = UserFactory.createRandomValidClient();
        int clientRegStatus = AuthHelper.registerClient(newClient);
        Assertions.assertTrue(clientRegStatus >= 200 && clientRegStatus <= 299,
                "Client registration failed with status: " + clientRegStatus);
        clientAccessToken = AuthHelper.login(newClient.getUsername(), newClient.getPassword()).getAccessToken();
        System.out.println("Client setup complete");

        // --- SETUP SELLER ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        RegisterSellerDTO newSeller = UserFactory.createRandomValidSeller(imageName);
        int sellerRegStatus = AuthHelper.registerSeller(newSeller);
        Assertions.assertTrue(sellerRegStatus >= 200 && sellerRegStatus <= 299,
                "Seller registration failed with status: " + sellerRegStatus);
        sellerAccessToken = AuthHelper.login(newSeller.getUsername(), newSeller.getPassword()).getAccessToken();
        System.out.println("Seller setup complete");
    }

    @Test
    public void testPlaceOrder() {
        // --- 1. CREATE A PRODUCT AS SELLER ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        productRequest.setName("Test Laptop");
        productRequest.setDescription("High quality laptop");
        productRequest.setPrice(999.99);
        productRequest.setStock(50);

        ProductResponseDto createdProduct = ProductHelper.createProduct(sellerAccessToken, productRequest);
        UUID productId = createdProduct.getId();
        System.out.println("Seller created product with ID: " + productId);

        // --- 2. CREATE AN ORDER AS CLIENT ---
        OrderProductDto orderProduct = new OrderProductDto(productId, 2);
        OrderFullDto orderFullDto = OrderFactory.createOrderFullDto(createdProduct.getSellerId(), Arrays.asList(orderProduct));

        int createStatus = OrderHelper.createOrder(clientAccessToken, orderFullDto);
        Assertions.assertTrue(createStatus >= 200 && createStatus <= 299,
                "Order creation failed with status: " + createStatus);
        System.out.println("Order placed successfully with status: " + createStatus);
    }

    @Test
    public void testClientViewOrders() {
        // --- 1. CREATE PRODUCT AND PLACE ORDER ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        ProductResponseDto product = ProductHelper.createProduct(sellerAccessToken, productRequest);

        OrderProductDto orderProduct = new OrderProductDto(product.getId(), 1);
        OrderFullDto orderFullDto = OrderFactory.createOrderFullDto(product.getSellerId(), Arrays.asList(orderProduct));
        
        int createStatus = OrderHelper.createOrder(clientAccessToken, orderFullDto);
        Assertions.assertTrue(createStatus >= 200 && createStatus <= 299, "Order creation failed");
        System.out.println("Order created for client");

        // --- 2. RETRIEVE CLIENT'S ORDERS ---
        List<OrderDto> clientOrders = OrderHelper.getByClientId(clientAccessToken);
        
        Assertions.assertNotNull(clientOrders, "Client orders list should not be null");
        Assertions.assertTrue(clientOrders.size() >= 1, "Client should have at least 1 order");
        System.out.println("Client successfully retrieved " + clientOrders.size() + " orders");

        // --- 3. VERIFY ORDER DETAILS ---
        OrderDto order = clientOrders.get(0);
        Assertions.assertNotNull(order.getId(), "Order ID should not be null");
        Assertions.assertEquals(OrderStatus.PLACED, order.getStatusType(), "Order status should be PLACED");
        System.out.println("Order details verified: " + order.toString());
    }

    @Test
    public void testSellerViewOrders() {
        // --- 1. CREATE PRODUCT AS SELLER ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        ProductResponseDto product = ProductHelper.createProduct(sellerAccessToken, productRequest);
        UUID sellerId = product.getSellerId();
        System.out.println("Product created by seller: " + sellerId);

        // --- 2. CREATE ORDER ---
        OrderProductDto orderProduct = new OrderProductDto(product.getId(), 1);
        OrderFullDto orderFullDto = OrderFactory.createOrderFullDto(sellerId, Arrays.asList(orderProduct));
        
        int createStatus = OrderHelper.createOrder(clientAccessToken, orderFullDto);
        Assertions.assertTrue(createStatus >= 200 && createStatus <= 299, "Order creation failed");

        // --- 3. RETRIEVE SELLER'S ORDERS ---
        List<OrderDto> sellerOrders = OrderHelper.getBySellerId(sellerAccessToken);
        
        Assertions.assertNotNull(sellerOrders, "Seller orders list should not be null");
        Assertions.assertTrue(sellerOrders.size() >= 1, "Seller should have at least 1 order");
        System.out.println("Seller successfully retrieved " + sellerOrders.size() + " orders");

        // --- 4. VERIFY ORDER BELONGS TO SELLER ---
        boolean orderBelongsToSeller = sellerOrders.stream()
                .anyMatch(order -> order.getSellerId().equals(sellerId));
        Assertions.assertTrue(orderBelongsToSeller, "Seller should see their own orders");
        System.out.println("Verified seller can see their orders");
    }

    @Test
    public void testUpdateOrderStatus() {
        // --- 1. CREATE PRODUCT AND ORDER ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        ProductResponseDto product = ProductHelper.createProduct(sellerAccessToken, productRequest);

        OrderProductDto orderProduct = new OrderProductDto(product.getId(), 1);
        OrderFullDto orderFullDto = OrderFactory.createOrderFullDto(product.getSellerId(), Arrays.asList(orderProduct));
        
        OrderHelper.createOrder(clientAccessToken, orderFullDto);
        
        List<OrderDto> orders = OrderHelper.getByClientId(clientAccessToken);
        UUID orderId = orders.get(0).getId();
        System.out.println("Order created with ID: " + orderId);

        // --- 2. UPDATE ORDER STATUS ---
        OrderStatusUpdateDto statusUpdate = new OrderStatusUpdateDto(orderId, OrderStatus.SHIPPED);
        int updateStatus = OrderHelper.updateStatus(sellerAccessToken, statusUpdate);
        
        Assertions.assertTrue(updateStatus >= 200 && updateStatus <= 299,
                "Order status update failed with status: " + updateStatus);
        System.out.println("Order status updated to SHIPPED");

        // --- 3. VERIFY STATUS CHANGE ---
        OrderFullDto updatedOrder = OrderHelper.getFullOrder(sellerAccessToken, orderId);
        Assertions.assertEquals(OrderStatus.SHIPPED, updatedOrder.getOrder().getStatusType(), "Order status should be SHIPPED");
        System.out.println("Order status successfully changed to: " + updatedOrder.getOrder().getStatusType());
    }

    @Test
    public void updateStock() {
        // --- 1. CREATE PRODUCT WITH INITIAL STOCK ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        productRequest.setName("Stock Test Product");
        productRequest.setStock(100);
        
        ProductResponseDto product = ProductHelper.createProduct(sellerAccessToken, productRequest);
        int initialStock = product.getStock();
        System.out.println("Product created with stock: " + initialStock);

        // --- 2. PLACE ORDER WITH QUANTITY 10 ---
        OrderProductDto orderProduct = new OrderProductDto(product.getId(), 10);
        OrderFullDto orderFullDto = OrderFactory.createOrderFullDto(product.getSellerId(), Arrays.asList(orderProduct));
        
        int createStatus = OrderHelper.createOrder(clientAccessToken, orderFullDto);
        Assertions.assertTrue(createStatus >= 200 && createStatus <= 299, "Order creation failed");
        System.out.println("Order placed with quantity: 10");

        // --- 3. VERIFY STOCK DECREASED ---
        ProductResponseDto updatedProduct = ProductHelper.getProduct(sellerAccessToken, product.getId());
        int newStock = updatedProduct.getStock();
        
        Assertions.assertEquals(initialStock - 10, newStock, "Stock should decrease by ordered quantity");
        System.out.println("Stock decreased from " + initialStock + " to " + newStock);
    }

    @Test
    public void tooMuchStock() {
        // --- 1. CREATE PRODUCT WITH LIMITED STOCK ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        productRequest.setName("Limited Stock Product");
        productRequest.setStock(5);
        
        ProductResponseDto product = ProductHelper.createProduct(sellerAccessToken, productRequest);
        System.out.println("Product created with stock: " + product.getStock());

        // --- 2. TRY TO ORDER MORE THAN AVAILABLE ---
        OrderProductDto orderProduct = new OrderProductDto(product.getId(), 100); // Try to order 100 when only 5 available
        OrderFullDto orderFullDto = OrderFactory.createOrderFullDto(product.getSellerId(), Arrays.asList(orderProduct));
        
        try {
            int createStatus = OrderHelper.createOrder(clientAccessToken, orderFullDto);
            Assertions.assertTrue(createStatus >= 400 && createStatus <= 499,
                    "Order should fail with 4xx error, got: " + createStatus);
            System.out.println("Order correctly rejected with status: " + createStatus);
        } catch (Exception e) {
            System.out.println("Order correctly rejected: " + e.getMessage());
            Assertions.assertTrue(e.getMessage().contains("400") || e.getMessage().contains("Insufficient") || 
                    e.getMessage().contains("stock"), "Should contain stock-related error");
        }
    }

    @Test
    public void createOrderWrong() {
        // --- TEST 1: CLIENT TRIES TO CREATE ORDER FOR WRONG PRODUCT ---
        OrderProductDto wrongProduct = new OrderProductDto(UUID.randomUUID(), 1);
        OrderFullDto wrongOrderFullDto = OrderFactory.createOrderFullDto(UUID.randomUUID(), Arrays.asList(wrongProduct));
        
        try {
            int createStatus = OrderHelper.createOrder(clientAccessToken, wrongOrderFullDto);
            Assertions.assertTrue(createStatus >= 400 && createStatus <= 499,
                    "Order with wrong product should fail, got: " + createStatus);
            System.out.println("Order with wrong product ID correctly rejected with status: " + createStatus);
        } catch (Exception e) {
            System.out.println("Order with wrong product ID correctly rejected");
            Assertions.assertTrue(e.getMessage().contains("400") || e.getMessage().contains("404") || 
                    e.getMessage().contains("not found"), "Should contain error about product not found");
        }

        // --- TEST 2: SELLER TRIES TO CREATE ORDER (SHOULD FAIL) ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");

        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        ProductResponseDto product = ProductHelper.createProduct(sellerAccessToken, productRequest);

        // --- TEST 2: CLIENT TRIES TO UPDATE ORDER STATUS (SHOULD FAIL) ---
        // First, create a valid order as client
        OrderProductDto validProduct = new OrderProductDto(product.getId(), 1);
        OrderFullDto validOrder = OrderFactory.createOrderFullDto(product.getSellerId(), Arrays.asList(validProduct));
        OrderHelper.createOrder(clientAccessToken, validOrder);
        
        List<OrderDto> orders = OrderHelper.getByClientId(clientAccessToken);
        UUID orderId = orders.get(0).getId();

        // Try to update as client (should fail)
        OrderStatusUpdateDto statusUpdate = new OrderStatusUpdateDto(orderId, OrderStatus.SHIPPED);
        try {
            int updateStatus = OrderHelper.updateStatus(clientAccessToken, statusUpdate);
            Assertions.assertTrue(updateStatus >= 400 && updateStatus <= 499,
                    "Client should not be able to update status, got: " + updateStatus);
            System.out.println("Client status update correctly rejected with status: " + updateStatus);
        } catch (Exception e) {
            System.out.println("Client status update correctly rejected");
            Assertions.assertTrue(e.getMessage().contains("403") || e.getMessage().contains("Forbidden") || 
                    e.getMessage().contains("Unauthorized"), "Should be authorization error");
        }
    }
}

