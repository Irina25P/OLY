package helpers;

import io.restassured.http.ContentType;

import java.io.File;

import static io.restassured.RestAssured.given;
import static constants.ApiEndpoints.*;

public class ImageHelper {

    /**
     * Uploads an image and returns the generated image name (objectName)
     */
    public static String uploadImage(File imageFile, String mimeType) {
        return given()
                .multiPart("file", imageFile, mimeType)
                .when()
                .post(IMAGES_UPLOAD)
                .then()
                .extract()
                .path("objectName");
    }

    /**
     * Uploads an image and returns the status code
     */
    public static int uploadImageStatusCode(File imageFile, String mimeType) {
        return given()
                .multiPart("file", imageFile, mimeType)
                .when()
                .post(IMAGES_UPLOAD)
                .then()
                .extract()
                .statusCode();
    }

    /**
     * Deletes an image and returns the status code
     */
    public static int deleteImage(String imageName, String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(IMAGES_UPLOAD + "/" + imageName)
                .then()
                .extract()
                .statusCode();
    }
}

