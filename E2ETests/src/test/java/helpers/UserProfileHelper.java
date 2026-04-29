package helpers;

import dtos.ClientDTO;
import dtos.SellerDTO;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static constants.ApiEndpoints.*;

public class UserProfileHelper {

    /**
     * Gets the seller profile using the access token
     */
    public static SellerDTO getSellerProfile(String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(USERS_SELLER_PROFILE)
                .then()
                .extract()
                .as(SellerDTO.class);
    }

    /**
     * Gets the client profile using the access token
     */
    public static ClientDTO getClientProfile(String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(USERS_CLIENT_PROFILE)
                .then()
                .extract()
                .as(ClientDTO.class);
    }

    /**
     * Deletes the seller profile
     */
    public static int deleteSellerProfile(String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(USERS_SELLER_PROFILE)
                .then()
                .extract()
                .statusCode();
    }

    /**
     * Deletes the client profile
     */
    public static int deleteClientProfile(String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(USERS_CLIENT_PROFILE)
                .then()
                .extract()
                .statusCode();
    }

    /**
     * Updates the seller profile
     */
    public static int updateSellerProfile(Object sellerDTO, String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(sellerDTO)
                .when()
                .put(USERS_SELLER_PROFILE)
                .then()
                .extract()
                .statusCode();
    }

    /**
     * Updates the seller profile and returns the updated seller DTO
     */
    public static Object updateSellerProfileAndGet(Object sellerDTO, String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(sellerDTO)
                .when()
                .put(USERS_SELLER_PROFILE)
                .then()
                .extract()
                .as(SellerDTO.class);
    }

    /**
     * Updates the client profile
     */
    public static int updateClientProfile(Object clientDTO, String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(clientDTO)
                .when()
                .put(USERS_CLIENT_PROFILE)
                .then()
                .extract()
                .statusCode();
    }

    /**
     * Updates the client profile and returns the updated client DTO
     */
    public static Object updateClientProfileAndGet(Object clientDTO, String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(clientDTO)
                .when()
                .put(USERS_CLIENT_PROFILE)
                .then()
                .extract()
                .as(ClientDTO.class);
    }
}

