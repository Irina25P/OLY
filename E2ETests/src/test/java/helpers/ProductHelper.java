package helpers;

import constants.InterestType;
import dtos.ProductRequestDto;
import dtos.ProductResponseDto;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static constants.ApiEndpoints.PRODUCTS;
import static constants.ApiEndpoints.PRODUCTS_SEARCH;
import static io.restassured.RestAssured.given;

public class ProductHelper {

    public static List<ProductResponseDto> getAllProducts(String accessToken, String name,
                                                     List<InterestType> categories,
                                                     Double minPrice,
                                                     Double maxPrice,
                                                     Double minRating) {
        Map<String, Object> queryParams = new HashMap<>();
        if (name != null) queryParams.put("name", name);
        if (minPrice != null) queryParams.put("minPrice", minPrice);
        if (maxPrice != null) queryParams.put("maxPrice", maxPrice);
        if (minRating != null) queryParams.put("minRating", minRating);
        if (categories != null && !categories.isEmpty()) {
            // RestAssured handles Lists by repeating the key: ?categories=A&categories=B
            String categoryString = categories.stream()
                    .map(Enum::name) // Sends "ELECTRONICS_IT"
                    .collect(Collectors.joining(","));
        }

        return given()
                .header("Authorization", "Bearer " + accessToken)
                .queryParams(queryParams)
                .when()
                .get(PRODUCTS_SEARCH)
                .then()
                .extract()
                .as(new TypeRef<>() {});
    }

    public static ProductResponseDto createProduct(String accessToken, ProductRequestDto productRequestDto) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(productRequestDto)
                .log().all()
                .when()
                .post(PRODUCTS)
                .then()
                .log().all()
                .extract()
                .as(ProductResponseDto.class);
    }

    public static ProductResponseDto updateProduct(String accessToken, ProductRequestDto productRequestDto, UUID id) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(productRequestDto)
                .when()
                .put(PRODUCTS + "/" + id.toString())
                .then()
                .log().all()
                .extract()
                .as(ProductResponseDto.class);
    }

    public static ProductResponseDto getProduct(String accessToken, UUID id) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get(PRODUCTS + "/" + id.toString())
                .then()
                .extract()
                .as(ProductResponseDto.class);
    }

    public static int deleteProduct(String accessToken, UUID id) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(PRODUCTS + "/" + id.toString())
                .then()
                .log().all()
                .extract()
                .statusCode();
    }
}
