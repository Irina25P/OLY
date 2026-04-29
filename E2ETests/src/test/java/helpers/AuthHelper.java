package helpers;

import dtos.AuthRequest;
import dtos.AuthResponse;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static constants.ApiEndpoints.*;

public class AuthHelper {

    /**
     * Registers a seller and returns the response status code
     */
    public static int registerSeller(Object sellerDTO) {
        return given()
                .contentType(ContentType.JSON)
                .body(sellerDTO)
                .when()
                .post(AUTH_REGISTER_SELLER)
                .then()
                .extract()
                .statusCode();
    }

    /**
     * Registers a client and returns the response status code
     */
    public static int registerClient(Object clientDTO) {
        return given()
                .contentType(ContentType.JSON)
                .body(clientDTO)
                .when()
                .post(AUTH_REGISTER_CLIENT)
                .then()
                .extract()
                .statusCode();
    }

    /**
     * Logs in a user and returns the AuthResponse with access token
     */
    public static AuthResponse login(String username, String password) {
        AuthRequest authRequest = new AuthRequest(username, password);
        return given()
                .contentType(ContentType.JSON)
                .body(authRequest)
                .when()
                .post(AUTH_LOGIN)
                .then()
                .extract()
                .as(AuthResponse.class);
    }

    /**
     * Logs in a user and returns just the status code
     */
    public static int loginStatusCode(String username, String password) {
        AuthRequest authRequest = new AuthRequest(username, password);
        return given()
                .contentType(ContentType.JSON)
                .body(authRequest)
                .when()
                .post(AUTH_LOGIN)
                .then()
                .extract()
                .statusCode();
    }
}

