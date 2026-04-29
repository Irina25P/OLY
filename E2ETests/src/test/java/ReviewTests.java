import constants.InterestType;
import dtos.*;
import factories.ProductFactory;
import factories.UserFactory;
import helpers.AuthHelper;
import helpers.ImageHelper;
import helpers.ProductHelper;
import helpers.ReviewHelper;
import helpers.UserProfileHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * E2E tests for review operations
 * - Add review
 * - Update review
 * - Delete review
 * - View product reviews
 */
public class ReviewTests extends BaseTest {

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
    public void testAddReview() {
        // --- 1. CREATE A PRODUCT AS SELLER ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        productRequest.setName("Test Product for Review");
        productRequest.setDescription("Product to receive reviews");
        
        ProductResponseDto product = ProductHelper.createProduct(sellerAccessToken, productRequest);
        UUID productId = product.getId();
        System.out.println("Product created with ID: " + productId);

        // --- 2. CLIENT CREATES A REVIEW ---
        CreateReviewRequest reviewRequest = CreateReviewRequest.builder()
                .productId(productId)
                .rating(5)
                .comment("Excellent product! Great quality and fast shipping.")
                .build();

        ReviewDetailDTO createdReview = ReviewHelper.createReviewDetailDTO(clientAccessToken, reviewRequest);
        
        Assertions.assertNotNull(createdReview.getId(), "Review ID should not be null");
        Assertions.assertEquals(5, createdReview.getRating(), "Rating should be 5");
        Assertions.assertEquals("Excellent product! Great quality and fast shipping.", createdReview.getComment(), "Comment mismatch");
        System.out.println("Review created successfully with ID: " + createdReview.getId());
    }

    @Test
    public void testGetProductReviews() {
        // --- 1. CREATE PRODUCT ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        ProductResponseDto product = ProductHelper.createProduct(sellerAccessToken, productRequest);
        UUID productId = product.getId();
        System.out.println("Product created");

        // --- 2. CREATE MULTIPLE REVIEWS ---
        CreateReviewRequest review1 = CreateReviewRequest.builder()
                .productId(productId)
                .rating(5)
                .comment("Amazing product!").build();

        CreateReviewRequest review2 = CreateReviewRequest.builder()
                .productId(productId)
                .rating(4)
                .comment("Very good, minor issues.").build();

        ReviewDetailDTO createdReview1 = ReviewHelper.createReviewDetailDTO(clientAccessToken, review1);
        System.out.println("Review 1 created");

        // Create second client to add another review
        RegisterClientDTO secondClient = UserFactory.createRandomValidClient();
        AuthHelper.registerClient(secondClient);
        String secondClientToken = AuthHelper.login(secondClient.getUsername(), secondClient.getPassword()).getAccessToken();
        
        ReviewDetailDTO createdReview2 = ReviewHelper.createReviewDetailDTO(secondClientToken, review2);
        System.out.println("Review 2 created");

        // --- 3. RETRIEVE PRODUCT REVIEWS ---
        List<ReviewDetailDTO> productReviews = ReviewHelper.getByProductId(clientAccessToken, productId);
        
        Assertions.assertNotNull(productReviews, "Reviews list should not be null");
        Assertions.assertTrue(productReviews.size() >= 2, "Should have at least 2 reviews");
        
        boolean foundReview1 = productReviews.stream().anyMatch(r -> r.getId().equals(createdReview1.getId()));
        boolean foundReview2 = productReviews.stream().anyMatch(r -> r.getId().equals(createdReview2.getId()));
        
        Assertions.assertTrue(foundReview1, "First review should be in the list");
        Assertions.assertTrue(foundReview2, "Second review should be in the list");
        System.out.println("Successfully retrieved " + productReviews.size() + " product reviews");
    }

    @Test
    public void testAddSellerReply() {
        // --- 1. CREATE PRODUCT ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        ProductResponseDto product = ProductHelper.createProduct(sellerAccessToken, productRequest);

        // --- 2. CLIENT CREATES REVIEW ---
        CreateReviewRequest reviewRequest = CreateReviewRequest.builder()
                .productId(product.getId())
                .rating(3)
                .comment("Average product, could be better").build();

        ReviewDetailDTO review = ReviewHelper.createReviewDetailDTO(clientAccessToken, reviewRequest);
        UUID reviewId = review.getId();
        System.out.println("Review created with ID: " + reviewId);

        // --- 3. SELLER REPLIES TO REVIEW ---
        CreateSellerReplyRequest replyRequest = CreateSellerReplyRequest.builder()
                .reviewId(reviewId)
                .reply("Thank you for the feedback! We're constantly improving our products.")
                .build();

        SellerReplyDetailDTO sellerReply = ReviewHelper.createSellerReplyDetailDTO(sellerAccessToken, replyRequest);
        
        Assertions.assertNotNull(sellerReply.getId(), "Seller reply ID should not be null");
        Assertions.assertEquals("Thank you for the feedback! We're constantly improving our products.", 
                sellerReply.getReply(), "Reply text mismatch");
        System.out.println("Seller reply created successfully with ID: " + sellerReply.getId());

        // --- 4. VERIFY REPLY IS ATTACHED TO REVIEW ---
        List<ReviewDetailDTO> productReviews = ReviewHelper.getByProductId(clientAccessToken, product.getId());
        ReviewDetailDTO reviewWithReply = productReviews.stream()
                .filter(r -> r.getId().equals(reviewId))
                .findFirst()
                .orElse(null);
        
        Assertions.assertNotNull(reviewWithReply, "Review should exist");
        Assertions.assertNotNull(reviewWithReply.getSellerReply(), "Seller reply should be attached");
        Assertions.assertEquals(sellerReply.getId(), reviewWithReply.getSellerReply().getId(), "Reply ID should match");
        System.out.println("Seller reply successfully attached to review");
    }

    @Test
    public void testDeleteReview() {
        // --- 1. CREATE PRODUCT AND REVIEW ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        ProductResponseDto product = ProductHelper.createProduct(sellerAccessToken, productRequest);

        CreateReviewRequest reviewRequest = CreateReviewRequest.builder()
                .productId(product.getId())
                .rating(2)
                .comment("Not satisfied with this purchase").build();

        ReviewDetailDTO review = ReviewHelper.createReviewDetailDTO(clientAccessToken, reviewRequest);
        UUID reviewId = review.getId();
        System.out.println("Review created with ID: " + reviewId);

        // --- 2. VERIFY REVIEW EXISTS ---
        List<ReviewDetailDTO> reviewsBefore = ReviewHelper.getByProductId(clientAccessToken, product.getId());
        Assertions.assertTrue(reviewsBefore.stream().anyMatch(r -> r.getId().equals(reviewId)), 
                "Review should exist before deletion");

        // --- 3. DELETE REVIEW ---
        int deleteStatus = ReviewHelper.deleteReview(clientAccessToken, reviewId);
        Assertions.assertTrue(deleteStatus >= 200 && deleteStatus <= 299,
                "Review deletion should succeed, got status: " + deleteStatus);
        System.out.println("Review deleted successfully with status: " + deleteStatus);

        // --- 4. VERIFY REVIEW IS DELETED ---
        List<ReviewDetailDTO> reviewsAfter = ReviewHelper.getByProductId(clientAccessToken, product.getId());
        Assertions.assertFalse(reviewsAfter.stream().anyMatch(r -> r.getId().equals(reviewId)), 
                "Review should not exist after deletion");
        System.out.println("Verified review was successfully deleted");
    }

    @Test
    public void testDeleteSellerReply() {
        // --- 1. CREATE PRODUCT, REVIEW, AND SELLER REPLY ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        ProductResponseDto product = ProductHelper.createProduct(sellerAccessToken, productRequest);

        CreateReviewRequest reviewRequest = CreateReviewRequest.builder()
                .productId(product.getId())
                .rating(4)
                .comment("Good product overall").build();

        ReviewDetailDTO review = ReviewHelper.createReviewDetailDTO(clientAccessToken, reviewRequest);

        CreateSellerReplyRequest replyRequest = CreateSellerReplyRequest.builder()
                .reviewId(review.getId())
                .reply("Thank you for your review!").build();

        SellerReplyDetailDTO sellerReply = ReviewHelper.createSellerReplyDetailDTO(sellerAccessToken, replyRequest);
        UUID replyId = sellerReply.getId();
        System.out.println("Seller reply created with ID: " + replyId);

        // --- 2. VERIFY REPLY IS ATTACHED ---
        List<ReviewDetailDTO> reviewsWithReply = ReviewHelper.getByProductId(clientAccessToken, product.getId());
        ReviewDetailDTO reviewBefore = reviewsWithReply.stream()
                .filter(r -> r.getId().equals(review.getId()))
                .findFirst().orElse(null);
        Assertions.assertNotNull(reviewBefore.getSellerReply(), "Reply should be attached before deletion");

        // --- 3. DELETE SELLER REPLY ---
        int deleteStatus = ReviewHelper.deleteSellerReply(sellerAccessToken, replyId);
        Assertions.assertTrue(deleteStatus >= 200 && deleteStatus <= 299,
                "Seller reply deletion should succeed, got status: " + deleteStatus);
        System.out.println("Seller reply deleted successfully with status: " + deleteStatus);

        // --- 4. VERIFY REPLY IS DELETED BUT REVIEW REMAINS ---
        List<ReviewDetailDTO> reviewsAfterDelete = ReviewHelper.getByProductId(clientAccessToken, product.getId());
        ReviewDetailDTO reviewAfter = reviewsAfterDelete.stream()
                .filter(r -> r.getId().equals(review.getId()))
                .findFirst().orElse(null);
        
        Assertions.assertNotNull(reviewAfter, "Review should still exist");
        Assertions.assertNull(reviewAfter.getSellerReply(), "Seller reply should be deleted");
        System.out.println("Verified seller reply was deleted but review remains");
    }

    @Test
    public void testMultipleReviewsAffectAverageRating() {
        // --- 1. CREATE PRODUCT ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        productRequest.setName("Multi-Review Product");
        productRequest.setStock(100);
        
        ProductResponseDto product = ProductHelper.createProduct(sellerAccessToken, productRequest);
        Double initialRating = product.getAverageRating();
        System.out.println("Product created with initial average rating: " + initialRating);

        // --- 2. CREATE MULTIPLE REVIEWS WITH DIFFERENT RATINGS ---
        int[] ratings = {5, 4, 3, 5, 4}; // Average will be 4.2
        List<ReviewDetailDTO> createdReviews = new java.util.ArrayList<>();
        
        for (int i = 0; i < ratings.length; i++) {
            // Create a new client for each review
            RegisterClientDTO client = UserFactory.createRandomValidClient();
            AuthHelper.registerClient(client);
            String clientToken = AuthHelper.login(client.getUsername(), client.getPassword()).getAccessToken();
            
            CreateReviewRequest reviewRequest = CreateReviewRequest.builder()
                    .productId(product.getId())
                    .rating(ratings[i])
                    .comment("Review " + (i + 1)).build();
            
            ReviewDetailDTO review = ReviewHelper.createReviewDetailDTO(clientToken, reviewRequest);
            createdReviews.add(review);
            System.out.println("Review " + (i + 1) + " created with rating: " + ratings[i]);
        }

        // --- 3. GET UPDATED PRODUCT AND CHECK AVERAGE RATING ---
        ProductResponseDto updatedProduct = ProductHelper.getProduct(sellerAccessToken, product.getId());
        Double newAverageRating = updatedProduct.getAverageRating();
        
        System.out.println("Product average rating after reviews: " + newAverageRating);
        Assertions.assertNotNull(newAverageRating, "Average rating should be calculated");
        
        // Expected average: (5 + 4 + 3 + 5 + 4) / 5 = 21 / 5 = 4.2
        Double expectedAverage = 4.2;
        Assertions.assertEquals(expectedAverage, newAverageRating, 0.1, 
                "Average rating should be approximately " + expectedAverage);
        
        System.out.println("Successfully verified average rating is calculated correctly");
    }

    @Test
    public void testCannotAddMultipleReviewsSameProduct() {
        // --- 1. CREATE PRODUCT ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        ProductResponseDto product = ProductHelper.createProduct(sellerAccessToken, productRequest);
        System.out.println("Product created");

        // --- 2. CLIENT CREATES FIRST REVIEW ---
        CreateReviewRequest firstReview = CreateReviewRequest.builder()
                .productId(product.getId())
                .rating(5)
                .comment("First review").build();

        ReviewDetailDTO review1 = ReviewHelper.createReviewDetailDTO(clientAccessToken, firstReview);
        System.out.println("First review created successfully");

        // --- 3. CLIENT TRIES TO CREATE SECOND REVIEW FOR SAME PRODUCT (SHOULD FAIL) ---
        CreateReviewRequest secondReview = CreateReviewRequest.builder()
                .productId(product.getId())
                .rating(3)
                .comment("Second review attempt").build();

        try {
            ReviewDetailDTO review2 = ReviewHelper.createReviewDetailDTO(clientAccessToken, secondReview);
            Assertions.fail("Client should not be able to create multiple reviews for same product");
        } catch (Exception e) {
            System.out.println("Second review creation correctly rejected: " + e.getMessage());
        }

        // --- 4. VERIFY ONLY ONE REVIEW EXISTS ---
        List<ReviewDetailDTO> productReviews = ReviewHelper.getByProductId(clientAccessToken, product.getId());
        long clientReviewCount = productReviews.stream()
                .filter(r -> r.getClientId().equals(review1.getClientId()))
                .count();
        
        Assertions.assertEquals(1, clientReviewCount, "Client should have exactly 1 review for this product");
        System.out.println("Verified client cannot create multiple reviews for same product");
    }

    @Test
    public void testCannotReplyToOwnReview() {
        // --- 1. CREATE PRODUCT AND REVIEW ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        ProductResponseDto product = ProductHelper.createProduct(sellerAccessToken, productRequest);

        CreateReviewRequest reviewRequest = CreateReviewRequest.builder()
                .productId(product.getId())
                .rating(4)
                .comment("Nice product").build();

        ReviewDetailDTO review = ReviewHelper.createReviewDetailDTO(clientAccessToken, reviewRequest);
        System.out.println("Review created");

        // --- 2. SELLER TRIES TO REPLY MULTIPLE TIMES (SHOULD FAIL SECOND TIME) ---
        CreateSellerReplyRequest firstReply = CreateSellerReplyRequest.builder()
                .reviewId(review.getId())
                .reply("First reply").build();

        SellerReplyDetailDTO reply1 = ReviewHelper.createSellerReplyDetailDTO(sellerAccessToken, firstReply);
        System.out.println("First seller reply created");

        // Try to add another reply (should fail)
        CreateSellerReplyRequest secondReply = CreateSellerReplyRequest.builder()
                .reviewId(review.getId())
                .reply("Second reply attempt").build();

        try {
            SellerReplyDetailDTO reply2 = ReviewHelper.createSellerReplyDetailDTO(sellerAccessToken, secondReply);
            Assertions.fail("Seller should not be able to create multiple replies to same review");
        } catch (Exception e) {
            System.out.println("Second reply creation correctly rejected: " + e.getMessage());
        }

        System.out.println("Verified seller cannot create multiple replies to same review");
    }

    @Test
    public void testDeleteProductRemovesReviews() {
        // --- 1. CREATE PRODUCT ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        productRequest.setName("Product to be Deleted");
        productRequest.setDescription("This product will be deleted");
        
        ProductResponseDto product = ProductHelper.createProduct(sellerAccessToken, productRequest);
        UUID productId = product.getId();
        System.out.println("Product created with ID: " + productId);

        // --- 2. CREATE MULTIPLE REVIEWS FOR THE PRODUCT ---
        // Review 1 from first client
        CreateReviewRequest review1Request = CreateReviewRequest.builder()
                .productId(productId)
                .rating(5)
                .comment("Great product!")
                .build();
        ReviewDetailDTO review1 = ReviewHelper.createReviewDetailDTO(clientAccessToken, review1Request);
        UUID review1Id = review1.getId();
        System.out.println("Review 1 created with ID: " + review1Id);

        // Review 2 from second client
        RegisterClientDTO secondClient = UserFactory.createRandomValidClient();
        AuthHelper.registerClient(secondClient);
        String secondClientToken = AuthHelper.login(secondClient.getUsername(), secondClient.getPassword()).getAccessToken();
        
        CreateReviewRequest review2Request = CreateReviewRequest.builder()
                .productId(productId)
                .rating(4)
                .comment("Good product")
                .build();
        ReviewDetailDTO review2 = ReviewHelper.createReviewDetailDTO(secondClientToken, review2Request);
        UUID review2Id = review2.getId();
        System.out.println("Review 2 created with ID: " + review2Id);

        // --- 3. VERIFY REVIEWS EXIST ---
        List<ReviewDetailDTO> reviewsBeforeDelete = ReviewHelper.getByProductId(clientAccessToken, productId);
        Assertions.assertEquals(2, reviewsBeforeDelete.size(), "Should have 2 reviews before deletion");
        System.out.println("Verified 2 reviews exist before product deletion");

        // --- 4. DELETE PRODUCT ---
        int deleteStatus = ProductHelper.deleteProduct(sellerAccessToken, productId);
        Assertions.assertTrue(deleteStatus >= 200 && deleteStatus <= 299,
                "Product deletion should succeed, got status: " + deleteStatus);
        System.out.println("Product deleted successfully with status: " + deleteStatus);

        // --- 5. VERIFY REVIEWS ARE DELETED ---
        List<ReviewDetailDTO> reviewsAfterDelete = ReviewHelper.getByProductId(clientAccessToken, productId);
        Assertions.assertTrue(reviewsAfterDelete.isEmpty(), 
                "All reviews should be deleted when product is deleted. Found: " + reviewsAfterDelete.size());
        System.out.println("Verified all reviews were deleted when product was deleted");

        // Verify specific review IDs are gone
        boolean review1Found = reviewsAfterDelete.stream().anyMatch(r -> r.getId().equals(review1Id));
        boolean review2Found = reviewsAfterDelete.stream().anyMatch(r -> r.getId().equals(review2Id));
        Assertions.assertFalse(review1Found, "Review 1 should be deleted");
        Assertions.assertFalse(review2Found, "Review 2 should be deleted");
        System.out.println("Confirmed individual reviews are removed");
    }

    @Test
    public void testDeleteClientRemovesReviews() {
        // --- 1. CREATE PRODUCT ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String imageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        
        ProductRequestDto productRequest = ProductFactory.createProductRequestDto(Arrays.asList(imageName));
        productRequest.setName("Product for Client Deletion Test");
        
        ProductResponseDto product = ProductHelper.createProduct(sellerAccessToken, productRequest);
        UUID productId = product.getId();
        System.out.println("Product created with ID: " + productId);

        // --- 2. CREATE A TEMPORARY CLIENT ---
        RegisterClientDTO tempClient = UserFactory.createRandomValidClient();
        int clientRegStatus = AuthHelper.registerClient(tempClient);
        Assertions.assertTrue(clientRegStatus >= 200 && clientRegStatus <= 299,
                "Client registration failed");
        
        String tempClientToken = AuthHelper.login(tempClient.getUsername(), tempClient.getPassword()).getAccessToken();
        System.out.println("Temporary client created and logged in");

        // --- 3. CREATE REVIEWS FROM TEMPORARY CLIENT ---
        CreateReviewRequest review1Request = CreateReviewRequest.builder()
                .productId(productId)
                .rating(5)
                .comment("Review 1 from temp client")
                .build();
        ReviewDetailDTO review1 = ReviewHelper.createReviewDetailDTO(tempClientToken, review1Request);
        UUID review1Id = review1.getId();
        System.out.println("Review 1 created from temp client with ID: " + review1Id);

        // Create another product and review from temp client
        String imageName2 = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        ProductRequestDto productRequest2 = ProductFactory.createProductRequestDto(Arrays.asList(imageName2));
        ProductResponseDto product2 = ProductHelper.createProduct(sellerAccessToken, productRequest2);
        
        CreateReviewRequest review2Request = CreateReviewRequest.builder()
                .productId(product2.getId())
                .rating(4)
                .comment("Review 2 from temp client")
                .build();
        ReviewDetailDTO review2 = ReviewHelper.createReviewDetailDTO(tempClientToken, review2Request);
        UUID review2Id = review2.getId();
        System.out.println("Review 2 created from temp client with ID: " + review2Id);

        // Create review from main client (should remain)
        CreateReviewRequest mainClientReviewRequest = CreateReviewRequest.builder()
                .productId(productId)
                .rating(3)
                .comment("Review from main client")
                .build();
        ReviewDetailDTO mainClientReview = ReviewHelper.createReviewDetailDTO(clientAccessToken, mainClientReviewRequest);
        UUID mainClientReviewId = mainClientReview.getId();
        System.out.println("Review from main client created with ID: " + mainClientReviewId);

        // --- 4. VERIFY REVIEWS EXIST ---
        List<ReviewDetailDTO> product1ReviewsBefore = ReviewHelper.getByProductId(clientAccessToken, productId);
        Assertions.assertTrue(product1ReviewsBefore.size() >= 2, 
                "Product 1 should have at least 2 reviews (temp + main client)");
        System.out.println("Verified reviews exist before client deletion");

        // --- 5. DELETE TEMPORARY CLIENT ---
        int deleteClientStatus = UserProfileHelper.deleteClientProfile(tempClientToken);
        Assertions.assertTrue(deleteClientStatus >= 200 && deleteClientStatus <= 299,
                "Client deletion should succeed, got status: " + deleteClientStatus);
        System.out.println("Temporary client deleted successfully with status: " + deleteClientStatus);

        // Wait for async propagation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // --- 6. VERIFY TEMP CLIENT'S REVIEWS ARE DELETED ---
        List<ReviewDetailDTO> product1ReviewsAfter = ReviewHelper.getByProductId(clientAccessToken, productId);
        
        // Review from temp client should be gone
        boolean tempClientReview1Found = product1ReviewsAfter.stream().anyMatch(r -> r.getId().equals(review1Id));
        Assertions.assertFalse(tempClientReview1Found, "Temp client's review should be deleted from product 1");
        System.out.println("Verified temp client's review was deleted from product 1");

        // Review from main client should still exist
        boolean mainClientReviewFound = product1ReviewsAfter.stream().anyMatch(r -> r.getId().equals(mainClientReviewId));
        Assertions.assertTrue(mainClientReviewFound, "Main client's review should still exist");
        System.out.println("Verified main client's review still exists");

        // --- 7. VERIFY TEMP CLIENT'S REVIEW ON PRODUCT 2 IS ALSO DELETED ---
        List<ReviewDetailDTO> product2ReviewsAfter = ReviewHelper.getByProductId(clientAccessToken, product2.getId());
        boolean tempClientReview2Found = product2ReviewsAfter.stream().anyMatch(r -> r.getId().equals(review2Id));
        Assertions.assertFalse(tempClientReview2Found, "Temp client's review should be deleted from product 2");
        System.out.println("Verified temp client's review was deleted from product 2");

        // --- 8. VERIFY ONLY TEMP CLIENT'S REVIEWS ARE DELETED ---
        long totalReviewsRemaining = product1ReviewsAfter.size() + product2ReviewsAfter.size();
        Assertions.assertEquals(1, totalReviewsRemaining, 
                "Only main client's review should remain across all products");
        System.out.println("Verified all temp client's reviews were deleted, main client's remain");
    }
}

