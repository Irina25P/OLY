package helpers;

import dtos.CreateReviewRequest;
import dtos.CreateSellerReplyRequest;
import dtos.ReviewDetailDTO;
import dtos.SellerReplyDetailDTO;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class ReviewHelper {

    public static ReviewDetailDTO createReviewDetailDTO(String accessToken, CreateReviewRequest request) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/reviews")
                .then()
                .extract()
                .as(ReviewDetailDTO.class);
    }

    public static List<ReviewDetailDTO> getByProductId(String accessToken, UUID productId) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/reviews/product/" + productId)
                .then()
                .extract()
                .as(new TypeRef<>() {});
    }

    public static Integer deleteReview(String accessToken, UUID reviewId) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/reviews/" + reviewId)
                .then()
                .extract()
                .statusCode();
    }

    public static SellerReplyDetailDTO createSellerReplyDetailDTO(String accessToken, CreateSellerReplyRequest request) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .log().all()
                .when()
                .post("/seller-replies")
                .then()
                .log().all()
                .extract()
                .as(SellerReplyDetailDTO.class);
    }

    public static Integer deleteSellerReply(String accessToken, UUID sellerReplyId) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/seller-replies/" + sellerReplyId)
                .then()
                .log().all()
                .extract()
                .statusCode();
    }
}
