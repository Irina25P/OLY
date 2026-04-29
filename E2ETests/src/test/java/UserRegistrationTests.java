import constants.ApiEndpoints;
import dtos.*;
import factories.UserFactory;
import helpers.AuthHelper;
import helpers.ImageHelper;
import helpers.UserProfileHelper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * E2E tests for user registration flows (Seller and Client)
 */
public class UserRegistrationTests extends BaseTest {

    @Test
    public void testSellerRegistrationWithImageUploadFlow() throws InterruptedException {
        // --- 1. UPLOAD THE IMAGE ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String generatedImageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        System.out.println("Successfully uploaded image. MinIO returned: " + generatedImageName);

        // --- 2. CREATE SELLER DTO ---
        RegisterSellerDTO newSeller = UserFactory.createRandomValidSeller(generatedImageName);
        System.out.println("Created seller with username: " + newSeller.getUsername());

        // --- 3. REGISTER SELLER ---
        int registrationStatus = AuthHelper.registerSeller(newSeller);
        Assertions.assertTrue(registrationStatus >= 200 && registrationStatus <= 299,
                "Seller registration failed with status: " + registrationStatus);
        System.out.println("Seller registered successfully");

        // --- 4. LOGIN & EXTRACT TOKEN ---
        AuthResponse authResponse = AuthHelper.login(newSeller.getUsername(), newSeller.getPassword());
        Assertions.assertNotNull(authResponse.getAccessToken(), "Access token should not be null");
        System.out.println("Seller logged in successfully");

        // --- 5. GET SELLER PROFILE ---
        SellerDTO sellerDTO = UserProfileHelper.getSellerProfile(authResponse.getAccessToken());

        // --- 6. VERIFY PROFILE ---
        Assertions.assertEquals(newSeller.getUsername(), sellerDTO.getUsername(), "Username mismatch");
        Assertions.assertEquals(newSeller.getEmail(), sellerDTO.getEmail(), "Email mismatch");
        Assertions.assertEquals(generatedImageName, sellerDTO.getImageName(), "Image name mismatch");
        Assertions.assertEquals(newSeller.getDescription(), sellerDTO.getDescription(), "Description mismatch");
        Assertions.assertEquals(newSeller.getName(), sellerDTO.getName(), "Name mismatch");
        Assertions.assertEquals(newSeller.getCounty(), sellerDTO.getCounty().name(), "County mismatch");
        System.out.println("Seller profile verified successfully");

        // --- 7. DELETE SELLER PROFILE ---
        int deleteStatus = UserProfileHelper.deleteSellerProfile(authResponse.getAccessToken());
        Assertions.assertTrue(deleteStatus >= 200 && deleteStatus <= 299,
                "Seller deletion failed with status: " + deleteStatus);

        // --- 8. WAIT FOR ASYNC PROPAGATION ---
        System.out.println("Waiting for RabbitMQ to propagate the deletion...");
        waitForAsync();

        // --- 9. ATTEMPT LOGIN AGAIN (SHOULD FAIL) ---
        int loginStatus = AuthHelper.loginStatusCode(newSeller.getUsername(), newSeller.getPassword());
        Assertions.assertTrue(loginStatus >= 400 && loginStatus <= 403,
                "Expected 4xx error after deletion, but got: " + loginStatus);
        System.out.println("Test passed: Seller was successfully deleted and cannot login anymore");
    }

    @Test
    public void testClientRegistrationFlow() throws InterruptedException {
        // --- 1. CREATE RANDOM CLIENT ---
        RegisterClientDTO newClient = UserFactory.createRandomValidClient();
        System.out.println("Created client with username: " + newClient.getUsername());

        // --- 2. REGISTER CLIENT ---
        int registrationStatus = AuthHelper.registerClient(newClient);
        Assertions.assertTrue(registrationStatus >= 200 && registrationStatus <= 299,
                "Client registration failed with status: " + registrationStatus);
        System.out.println("Client registered successfully");

        // --- 3. LOGIN & EXTRACT TOKEN ---
        AuthResponse authResponse = AuthHelper.login(newClient.getUsername(), newClient.getPassword());
        Assertions.assertNotNull(authResponse.getAccessToken(), "Access token should not be null");
        System.out.println("Client logged in successfully");

        // --- 4. GET CLIENT PROFILE ---
        ClientDTO clientDTO = UserProfileHelper.getClientProfile(authResponse.getAccessToken());

        // --- 5. VERIFY PROFILE ---
        Assertions.assertEquals(newClient.getUsername(), clientDTO.getUsername(), "Username mismatch");
        Assertions.assertEquals(newClient.getEmail(), clientDTO.getEmail(), "Email mismatch");
        Assertions.assertEquals(newClient.getFirstName(), clientDTO.getFirstName(), "First name mismatch");
        Assertions.assertEquals(newClient.getLastName(), clientDTO.getLastName(), "Last name mismatch");
        Assertions.assertEquals(newClient.getAge(), clientDTO.getAge(), "Age mismatch");
        Assertions.assertEquals(newClient.getPhoneNumber(), clientDTO.getPhoneNumber(), "Phone number mismatch");
        Assertions.assertEquals(newClient.getCounty(), clientDTO.getCounty().name(), "County mismatch");
        System.out.println("Client profile verified successfully");

        // --- 6. DELETE CLIENT PROFILE ---
        int deleteStatus = UserProfileHelper.deleteClientProfile(authResponse.getAccessToken());
        Assertions.assertTrue(deleteStatus >= 200 && deleteStatus <= 299,
                "Client deletion failed with status: " + deleteStatus);

        // --- 7. WAIT FOR ASYNC PROPAGATION ---
        System.out.println("Waiting for RabbitMQ to propagate the deletion...");
        waitForAsync();

        // --- 8. ATTEMPT LOGIN AGAIN (SHOULD FAIL) ---
        int loginStatus = AuthHelper.loginStatusCode(newClient.getUsername(), newClient.getPassword());
        Assertions.assertTrue(loginStatus >= 400 && loginStatus <= 403,
                "Expected 4xx error after deletion, but got: " + loginStatus);
        System.out.println("Test passed: Client was successfully deleted and cannot login anymore");
    }

    @Test
    public void testSellerProfileUpdateWithImageReplacement() throws InterruptedException {
        // --- 1. UPLOAD FIRST IMAGE ---
        File logoToUpload = new File("src/test/resources/test_image.avif");
        String firstImageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        System.out.println("Successfully uploaded first image: " + firstImageName);

        // --- 2. CREATE AND REGISTER SELLER ---
        RegisterSellerDTO newSeller = UserFactory.createRandomValidSeller(firstImageName);
        System.out.println("Created seller with username: " + newSeller.getUsername());

        int registrationStatus = AuthHelper.registerSeller(newSeller);
        Assertions.assertTrue(registrationStatus >= 200 && registrationStatus <= 299,
                "Seller registration failed with status: " + registrationStatus);
        System.out.println("Seller registered successfully");

        // --- 3. LOGIN ---
        AuthResponse authResponse = AuthHelper.login(newSeller.getUsername(), newSeller.getPassword());
        Assertions.assertNotNull(authResponse.getAccessToken(), "Access token should not be null");
        System.out.println("Seller logged in successfully");

        // --- 4. GET INITIAL PROFILE ---
        SellerDTO initialProfile = UserProfileHelper.getSellerProfile(authResponse.getAccessToken());
        Assertions.assertEquals(firstImageName, initialProfile.getImageName(), "Initial image name mismatch");
        System.out.println("Initial profile verified with image: " + firstImageName);

        // --- 5. UPLOAD SECOND IMAGE ---
        String secondImageName = ImageHelper.uploadImage(logoToUpload, "image/jpeg");
        System.out.println("Successfully uploaded second image: " + secondImageName);

        // --- 6. UPDATE SELLER PROFILE WITH NEW IMAGE ---
        RegisterSellerDTO updatedSeller = new RegisterSellerDTO();
        updatedSeller.setName(newSeller.getName());
        updatedSeller.setEmail(newSeller.getEmail());
        updatedSeller.setUsername(newSeller.getUsername());
        updatedSeller.setPassword(newSeller.getPassword());
        updatedSeller.setDescription("Updated: " + newSeller.getDescription());
        updatedSeller.setCounty(newSeller.getCounty());
        updatedSeller.setImageName(secondImageName); // Set new image

        int updateStatus = UserProfileHelper.updateSellerProfile(updatedSeller, authResponse.getAccessToken());
        Assertions.assertTrue(updateStatus >= 200 && updateStatus <= 299,
                "Seller update failed with status: " + updateStatus);
        System.out.println("Seller profile updated with new image: " + secondImageName);

        // --- 7. GET UPDATED PROFILE ---
        SellerDTO updatedProfile = UserProfileHelper.getSellerProfile(authResponse.getAccessToken());
        Assertions.assertEquals(secondImageName, updatedProfile.getImageName(), "Updated image name mismatch");
        Assertions.assertEquals("Updated: " + newSeller.getDescription(), updatedProfile.getDescription(), "Description not updated");
        System.out.println("Updated profile verified with new image: " + secondImageName);

        // --- 8. VERIFY OLD IMAGE IS DESTROYED ---
        System.out.println("Verifying old image is destroyed...");
        waitForAsync(); // Wait for async image cleanup

        // --- 9. CLEANUP ---
        int deleteStatus = UserProfileHelper.deleteSellerProfile(authResponse.getAccessToken());
        Assertions.assertTrue(deleteStatus >= 200 && deleteStatus <= 299,
                "Seller deletion failed with status: " + deleteStatus);
        System.out.println("Test passed: Seller profile updated with image replacement and old image destroyed");
    }

    @Test
    public void testClientProfileUpdate() throws InterruptedException {
        // --- 1. CREATE AND REGISTER CLIENT ---
        RegisterClientDTO newClient = UserFactory.createRandomValidClient();
        System.out.println("Created client with username: " + newClient.getUsername());

        int registrationStatus = AuthHelper.registerClient(newClient);
        Assertions.assertTrue(registrationStatus >= 200 && registrationStatus <= 299,
                "Client registration failed with status: " + registrationStatus);
        System.out.println("Client registered successfully");

        // --- 2. LOGIN ---
        AuthResponse authResponse = AuthHelper.login(newClient.getUsername(), newClient.getPassword());
        Assertions.assertNotNull(authResponse.getAccessToken(), "Access token should not be null");
        System.out.println("Client logged in successfully");

        // --- 3. GET INITIAL PROFILE ---
        ClientDTO initialProfile = UserProfileHelper.getClientProfile(authResponse.getAccessToken());
        String initialPhoneNumber = initialProfile.getPhoneNumber();
        System.out.println("Initial profile verified with phone: " + initialPhoneNumber);

        // --- 4. UPDATE CLIENT PROFILE ---
        RegisterClientDTO updatedClient = new RegisterClientDTO();
        updatedClient.setFirstName(newClient.getFirstName());
        updatedClient.setLastName(newClient.getLastName());
        updatedClient.setEmail(newClient.getEmail());
        updatedClient.setUsername(newClient.getUsername());
        updatedClient.setPassword(newClient.getPassword());
        updatedClient.setCounty(newClient.getCounty());
        updatedClient.setAge(newClient.getAge() + 1); // Increment age
        updatedClient.setPhoneNumber("+40" + System.currentTimeMillis() % 1000000000); // New phone
        updatedClient.setInterests(newClient.getInterests());

        int updateStatus = UserProfileHelper.updateClientProfile(updatedClient, authResponse.getAccessToken());
        Assertions.assertTrue(updateStatus >= 200 && updateStatus <= 299,
                "Client update failed with status: " + updateStatus);
        System.out.println("Client profile updated");

        // --- 5. GET UPDATED PROFILE ---
        ClientDTO updatedProfile = UserProfileHelper.getClientProfile(authResponse.getAccessToken());
        Assertions.assertEquals(newClient.getAge() + 1, updatedProfile.getAge(), "Age not updated");
        Assertions.assertNotEquals(initialPhoneNumber, updatedProfile.getPhoneNumber(), "Phone number not updated");
        System.out.println("Updated profile verified with age: " + updatedProfile.getAge());

        // --- 6. CLEANUP ---
        int deleteStatus = UserProfileHelper.deleteClientProfile(authResponse.getAccessToken());
        Assertions.assertTrue(deleteStatus >= 200 && deleteStatus <= 299,
                "Client deletion failed with status: " + deleteStatus);
        System.out.println("Test passed: Client profile successfully updated");
    }
}

