package factories;

import dtos.CreateReviewRequest;
import dtos.CreateSellerReplyRequest;
import net.datafaker.Faker;

import java.util.Random;
import java.util.UUID;

public class ReviewFactory {
    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    public static CreateReviewRequest createReviewRequest(UUID productId) {
        return new CreateReviewRequest(
                productId,
                faker.number().numberBetween(1, 5),
                faker.company().catchPhrase()
        );
    }

    public static CreateSellerReplyRequest createSellerReplyRequest(UUID reviewId) {
        return new CreateSellerReplyRequest(
                reviewId,
                faker.company().catchPhrase()
        );
    }
}
