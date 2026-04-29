import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseTest {

    @BeforeAll
    public static void setupBase() {
        // Check if we are in Docker or Local

        RestAssured.baseURI = "http://traefik/api/v1";
        RestAssured.port = 80;
    }

    /**
     * Utility method to wait for async operations (like RabbitMQ propagation)
     */
    protected static void waitForAsync(long milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }

    /**
     * Wait for 2 seconds (default async wait time)
     */
    protected static void waitForAsync() throws InterruptedException {
        waitForAsync(2000);
    }
}

