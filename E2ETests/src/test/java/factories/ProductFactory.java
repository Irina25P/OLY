package factories;

import constants.InterestType;
import dtos.ProductRequestDto;
import net.datafaker.Faker;

import java.util.List;
import java.util.Random;

public class ProductFactory {
    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    public static ProductRequestDto  createProductRequestDto(List<String> imageNames) {
        String name = faker.name().fullName();
        String description = faker.name().fullName();

        return new ProductRequestDto(
                name,
                description,
                faker.number().randomDouble(2, 10, 1000),
                faker.number().numberBetween(10, 100),
                imageNames,
                InterestType.ELECTRONICS_IT
        );
    }
}
