package factories;

import constants.County;
import constants.InterestType;
import dtos.RegisterClientDTO;
import dtos.RegisterSellerDTO;
import net.datafaker.Faker;

import java.util.List;
import java.util.Random;

public class UserFactory {
    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    public static RegisterClientDTO createRandomValidClient() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();

        // Generate a random but realistic email based on the name
        String email = faker.internet().emailAddress(firstName.toLowerCase() + "." + lastName.toLowerCase());

        return new RegisterClientDTO(
                firstName,
                lastName,
                email,
                faker.internet().username(),
                faker.internet().password(8, 20, true, true, true), // Strong random password
                getRandomCounty(), // Helper method below
                faker.number().numberBetween(18, 65),
                faker.phoneNumber().cellPhone(),
                List.of(InterestType.ELECTRONICS_IT.name())
        );
    }

    public static RegisterSellerDTO createRandomValidSeller(String imageName) {
        String companyName = faker.company().name();

        return new RegisterSellerDTO(
                companyName,
                faker.internet().emailAddress(),
                faker.internet().username(),
                faker.internet().password(8, 20, true, true, true),
                imageName,
                faker.company().catchPhrase(), // Gives a random description like "Synergistic IT solutions"
                getRandomCounty()
        );
    }

    private static String getRandomCounty() {
        County[] counties = County.values();
        // Pick a random enum value from your County class
        County randomCounty = counties[random.nextInt(counties.length)];
        return randomCounty.name();
    }
}
