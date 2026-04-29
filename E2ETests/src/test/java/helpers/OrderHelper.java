package helpers;

import dtos.OrderDto;
import dtos.OrderFullDto;
import dtos.OrderStatusUpdateDto;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class OrderHelper {
    public static List<OrderDto> getBySellerId(String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/orders/seller")
                .then()
                .extract()
                .as(new TypeRef<>() {});
    }

    public static List<OrderDto> getByClientId(String accessToken) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/orders/client")
                .then()
                .extract()
                .as(new TypeRef<>() {});
    }

    public static OrderFullDto getFullOrder(String accessToken, UUID orderId) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/orders/" + orderId)
                .then()
                .extract()
                .as(OrderFullDto.class);
    }

    public static Integer createOrder(String accessToken, OrderFullDto orderFullDto) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(orderFullDto)
                .when()
                .post("/orders")
                .then()
                .extract()
                .statusCode();
    }

    public static Integer updateStatus(String accessToken, OrderStatusUpdateDto  orderStatusUpdateDto) {
        return given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(orderStatusUpdateDto)
                .when()
                .put("/orders")
                .then()
                .extract()
                .statusCode();
    }
}
