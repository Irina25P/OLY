package com.example.authmicroservice;

import com.example.authmicroservice.consumer.DeleteUserConsumer;
import com.example.authmicroservice.entities.User;
import com.example.authmicroservice.repositories.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @MockitoBean
    private ConnectionFactory connectionFactory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeleteUserConsumer deleteUserConsumer;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        userRepository.deleteAll();
    }

    // ==================== REGISTER TESTS ====================

    @Test
    void registerAdmin_shouldReturn201() throws Exception {
        String json = """
                {
                    "username": "admin1",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/v1/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string("Admin Added Successfully"));
    }

    @Test
    void registerSeller_shouldReturn201() throws Exception {
        String json = """
                {
                    "name": "My Shop",
                    "email": "seller@example.com",
                    "username": "seller1",
                    "password": "password123",
                    "logoURL": "https://example.com/logo.png",
                    "description": "Best shop",
                    "region": "EU"
                }
                """;

        mockMvc.perform(post("/v1/auth/register/seller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string("Seller Added Successfully"));
    }

    @Test
    void registerClient_shouldReturn201() throws Exception {
        String json = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john@example.com",
                    "username": "client1",
                    "password": "password123",
                    "region": "EU",
                    "age": 25,
                    "phoneNumber": "+40712345678",
                    "interests": ["electronics", "books"]
                }
                """;

        mockMvc.perform(post("/v1/auth/register/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string("Client Added Successfully"));
    }

    @Test
    void registerDuplicateUsername_shouldReturn400() throws Exception {
        String json = """
                {
                    "username": "admin1",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/v1/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // ==================== LOGIN TESTS ====================

    @Test
    void loginAdmin_shouldReturnToken() throws Exception {
        registerUser("/v1/auth/register/admin", """
                {
                    "username": "admin1",
                    "password": "password123"
                }
                """);

        String loginJson = """
                {
                    "username": "admin1",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void loginSeller_shouldReturnToken() throws Exception {
        registerUser("/v1/auth/register/seller", """
                {
                    "name": "My Shop",
                    "email": "seller@example.com",
                    "username": "seller1",
                    "password": "password123",
                    "logoURL": "https://example.com/logo.png",
                    "description": "Best shop",
                    "region": "EU"
                }
                """);

        String loginJson = """
                {
                    "username": "seller1",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void loginClient_shouldReturnToken() throws Exception {
        registerUser("/v1/auth/register/client", """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john@example.com",
                    "username": "client1",
                    "password": "password123",
                    "region": "EU",
                    "age": 25,
                    "phoneNumber": "+40712345678",
                    "interests": ["electronics", "books"]
                }
                """);

        String loginJson = """
                {
                    "username": "client1",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void loginInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        String loginJson = """
                {
                    "username": "nonexistent",
                    "password": "wrongpassword"
                }
                """;

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    // ==================== VERIFY TESTS ====================

    @Test
    void verifyTokenAdmin_shouldReturnCorrectHeaders() throws Exception {
        registerUser("/v1/auth/register/admin", """
                {
                    "username": "admin1",
                    "password": "password123"
                }
                """);

        String token = loginAndGetToken("admin1", "password123");

        mockMvc.perform(get("/v1/auth/verify")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().string("Username-Header", "admin1"))
                .andExpect(header().string("Role-Header", "ADMIN"))
                .andExpect(header().exists("UserId-Header"));
    }

    @Test
    void verifyTokenSeller_shouldReturnCorrectHeaders() throws Exception {
        registerUser("/v1/auth/register/seller", """
                {
                    "name": "My Shop",
                    "email": "seller@example.com",
                    "username": "seller1",
                    "password": "password123",
                    "logoURL": "https://example.com/logo.png",
                    "description": "Best shop",
                    "region": "EU"
                }
                """);

        String token = loginAndGetToken("seller1", "password123");

        mockMvc.perform(get("/v1/auth/verify")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().string("Username-Header", "seller1"))
                .andExpect(header().string("Role-Header", "SELLER"))
                .andExpect(header().exists("UserId-Header"));
    }

    @Test
    void verifyTokenClient_shouldReturnCorrectHeaders() throws Exception {
        registerUser("/v1/auth/register/client", """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john@example.com",
                    "username": "client1",
                    "password": "password123",
                    "region": "EU",
                    "age": 25,
                    "phoneNumber": "+40712345678",
                    "interests": ["electronics", "books"]
                }
                """);

        String token = loginAndGetToken("client1", "password123");

        mockMvc.perform(get("/v1/auth/verify")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().string("Username-Header", "client1"))
                .andExpect(header().string("Role-Header", "CLIENT"))
                .andExpect(header().exists("UserId-Header"));
    }

    @Test
    void verifyToken_noAuthorizationHeader_shouldReturn401() throws Exception {
        mockMvc.perform(get("/v1/auth/verify"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void verifyToken_invalidToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/v1/auth/verify")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void verifyToken_missingBearerPrefix_shouldReturn401() throws Exception {
        registerUser("/v1/auth/register/admin", """
                {
                    "username": "admin1",
                    "password": "password123"
                }
                """);

        String token = loginAndGetToken("admin1", "password123");

        mockMvc.perform(get("/v1/auth/verify")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isUnauthorized());
    }

    // ==================== DELETE USER CONSUMER TESTS ====================

    @Test
    void deleteUserConsumer_shouldDeleteExistingUser() throws Exception {
        // Register a client
        registerUser("/v1/auth/register/client", """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john@example.com",
                    "username": "client1",
                    "password": "password123",
                    "region": "EU",
                    "age": 25,
                    "phoneNumber": "+40712345678",
                    "interests": ["electronics", "books"]
                }
                """);

        // Get the user from DB to obtain their ID
        User user = userRepository.findByUsername("client1").orElseThrow();

        // Simulate receiving a delete message from RabbitMQ
        String deleteMessage = """
                {
                    "id": "%s"
                }
                """.formatted(user.getId().toString());

        deleteUserConsumer.consumeDeleteUserMessage(deleteMessage);

        // Verify the user was deleted
        Optional<User> deletedUser = userRepository.findByUsername("client1");
        assertTrue(deletedUser.isEmpty(), "User should have been deleted from the database");
    }

    @Test
    void deleteUserConsumer_shouldHandleNonExistentUser() throws Exception {
        // Simulate receiving a delete message for a user that doesn't exist
        String deleteMessage = """
                {
                    "id": "00000000-0000-0000-0000-000000000000"
                }
                """;

        // Should not throw — error is caught internally by the consumer
        assertDoesNotThrow(() -> deleteUserConsumer.consumeDeleteUserMessage(deleteMessage));
    }

    @Test
    void deleteUserConsumer_deletedUserCannotLogin() throws Exception {
        // Register an admin
        registerUser("/v1/auth/register/admin", """
                {
                    "username": "admin1",
                    "password": "password123"
                }
                """);

        // Verify login works before deletion
        loginAndGetToken("admin1", "password123");

        // Get user ID and simulate delete message
        User user = userRepository.findByUsername("admin1").orElseThrow();
        String deleteMessage = """
                {
                    "id": "%s"
                }
                """.formatted(user.getId().toString());

        deleteUserConsumer.consumeDeleteUserMessage(deleteMessage);

        // Verify login fails after deletion
        String loginJson = """
                {
                    "username": "admin1",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    // ==================== HELPER METHODS ====================

    private void registerUser(String path, String json) throws Exception {
        mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String loginJson = """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """.formatted(username, password);

        MvcResult result = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return JsonPath.read(responseBody, "$.accessToken");
    }
}





