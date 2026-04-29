package org.example.usermicroservice;

import org.example.usermicroservice.dtos.ClientDTO;
import org.example.usermicroservice.dtos.DeleteUserDTO;
import org.example.usermicroservice.dtos.SellerDTO;
import org.example.usermicroservice.entities.Client;
import org.example.usermicroservice.entities.Interest;
import org.example.usermicroservice.entities.InterestType;
import org.example.usermicroservice.entities.County;
import org.example.usermicroservice.entities.Seller;
import org.example.usermicroservice.repositories.ClientRepository;
import org.example.usermicroservice.repositories.SellerRepository;
import org.example.usermicroservice.services.ClientService;
import org.example.usermicroservice.services.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(TestSecurityConfig.class)
public class EndToEndIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    private static final String USER_ID_HEADER = "UserId-Header";

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        clientRepository.deleteAll();
        sellerRepository.deleteAll();
        reset(rabbitTemplate);
    }

    @Test
    @WithMockUser(username = "e2eclient", roles = {"CLIENT"})
    public void testCompleteClientWorkflow() throws Exception {
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);
        client.setUsername("e2eclient");
        client.setFirstName("End");
        client.setLastName("ToEnd");
        client.setEmail("e2e@client.com");
        client.setCounty(County.BUCURESTI);
        client.setAge(30);
        client.setPhoneNumber("0712121212");

        List<Interest> interests = new ArrayList<>();
        Interest interest = new Interest();
        interest.setId(UUID.randomUUID());
        interest.setInterestType(InterestType.ELECTRONICS_IT);
        interests.add(interest);
        client.setInterests(interests);

        clientRepository.save(client);

        mockMvc.perform(get("/v1/users/client/profile")
                        .header(USER_ID_HEADER, clientId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("e2eclient")))
                .andExpect(jsonPath("$.firstName", is("End")))
                .andExpect(jsonPath("$.email", is("e2e@client.com")));

        ClientDTO updateDTO = new ClientDTO();
        updateDTO.setFirstName("UpdatedEnd");
        updateDTO.setEmail("updated@client.com");
        updateDTO.setAge(31);

        mockMvc.perform(put("/v1/users/client/profile")
                        .header(USER_ID_HEADER, clientId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("UpdatedEnd")))
                .andExpect(jsonPath("$.email", is("updated@client.com")))
                .andExpect(jsonPath("$.age", is(31)));

        ClientDTO retrieved = clientService.getClientProfile(clientId);
        assertEquals("UpdatedEnd", retrieved.getFirstName());
        assertEquals("updated@client.com", retrieved.getEmail());
        assertEquals(31, retrieved.getAge());

        mockMvc.perform(delete("/v1/users/client/profile")
                        .header(USER_ID_HEADER, clientId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(clientRepository.existsById(clientId));

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("deleteUser-Topic-Exchange"),
                eq("deleteUser"),
                argThat((Object msg) -> msg instanceof DeleteUserDTO && ((DeleteUserDTO) msg).getId().equals(clientId))
        );
    }

    @Test
    @WithMockUser(username = "e2eseller", roles = {"SELLER"})
    public void testCompleteSellerWorkflow() throws Exception {
        UUID sellerId = UUID.randomUUID();
        Seller seller = new Seller();
        seller.setId(sellerId);
        seller.setUsername("e2eseller");
        seller.setEmail("e2e@seller.com");
        seller.setImageName("https://example.com/logo.png");
        seller.setDescription("E2E seller");
        seller.setCounty(County.TIMIS);

        sellerRepository.save(seller);

        mockMvc.perform(get("/v1/users/seller/profile")
                        .header(USER_ID_HEADER, sellerId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("e2eseller")))
                .andExpect(jsonPath("$.email", is("e2e@seller.com")));

        SellerDTO updateDTO = new SellerDTO();
        updateDTO.setEmail("updated@seller.com");
        updateDTO.setDescription("Updated E2E seller");

        mockMvc.perform(put("/v1/users/seller/profile")
                        .header(USER_ID_HEADER, sellerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("updated@seller.com")))
                .andExpect(jsonPath("$.description", is("Updated E2E seller")));

        SellerDTO retrieved = sellerService.getSellerProfile(sellerId);
        assertEquals("updated@seller.com", retrieved.getEmail());
        assertEquals("Updated E2E seller", retrieved.getDescription());

        mockMvc.perform(delete("/v1/users/seller/profile")
                        .header(USER_ID_HEADER, sellerId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(sellerRepository.existsById(sellerId));

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("deleteUser-Topic-Exchange"),
                eq("deleteUser"),
                argThat((Object msg) -> msg instanceof DeleteUserDTO && ((DeleteUserDTO) msg).getId().equals(sellerId))
        );
    }

    @Test
    @WithMockUser(username = "multiuser", roles = {"CLIENT", "SELLER"})
    public void testMultipleClientsAndSellers() throws Exception {
        List<UUID> clientIds = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            UUID clientId = UUID.randomUUID();
            clientIds.add(clientId);

            Client client = new Client();
            client.setId(clientId);
            client.setUsername("client" + i);
            client.setFirstName("Client" + i);
            client.setLastName("Last" + i);
            client.setEmail("client" + i + "@example.com");
            client.setCounty(County.BUCURESTI);
            client.setAge(25 + i);
            client.setPhoneNumber("071" + (2000000 + i));

            clientRepository.save(client);
        }

        List<UUID> sellerIds = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            UUID sellerId = UUID.randomUUID();
            sellerIds.add(sellerId);

            Seller seller = new Seller();
            seller.setId(sellerId);
            seller.setUsername("seller" + i);
            seller.setEmail("seller" + i + "@example.com");
            seller.setImageName("https://example.com/logo" + i + ".png");
            seller.setDescription("Seller " + i);
            seller.setCounty(County.TIMIS);

            sellerRepository.save(seller);
        }

        for (UUID clientId : clientIds) {
            mockMvc.perform(get("/v1/users/client/profile")
                            .header(USER_ID_HEADER, clientId.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        for (UUID sellerId : sellerIds) {
            mockMvc.perform(get("/v1/users/seller/profile")
                            .header(USER_ID_HEADER, sellerId.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        assertEquals(3, clientRepository.count());
        assertEquals(3, sellerRepository.count());
    }

    @Test
    @WithMockUser(username = "concurrent", roles = {"CLIENT"})
    public void testConcurrentClientUpdates() {
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);
        client.setUsername("concurrent");
        client.setFirstName("Concurrent");
        client.setLastName("Client");
        client.setEmail("concurrent@example.com");
        client.setCounty(County.BUCURESTI);
        client.setAge(25);
        client.setPhoneNumber("0712345678");

        clientRepository.save(client);

        for (int i = 0; i < 5; i++) {
            ClientDTO updateDTO = new ClientDTO();
            updateDTO.setAge(25 + i);
            updateDTO.setEmail("update" + i + "@example.com");

            ClientDTO updated = clientService.updateClientProfile(clientId, updateDTO);
            assertEquals(25 + i, updated.getAge());
            assertEquals("update" + i + "@example.com", updated.getEmail());
        }

        ClientDTO final_state = clientService.getClientProfile(clientId);
        assertEquals(29, final_state.getAge());
        assertEquals("update4@example.com", final_state.getEmail());
    }

    @Test
    @WithMockUser(username = "interests", roles = {"CLIENT"})
    public void testClientWithInterestManagement() throws Exception {
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);
        client.setUsername("interests");
        client.setFirstName("Interest");
        client.setLastName("Client");
        client.setEmail("interests@example.com");
        client.setCounty(County.BUCURESTI);
        client.setAge(25);
        client.setPhoneNumber("0712345678");

        List<Interest> interests = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Interest interest = new Interest();
            interest.setId(UUID.randomUUID());
            interest.setInterestType(InterestType.values()[i]);
            interests.add(interest);
        }
        client.setInterests(interests);

        clientRepository.save(client);

        mockMvc.perform(get("/v1/users/client/profile")
                        .header(USER_ID_HEADER, clientId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interests", hasSize(3)));
    }

    @Test
    @WithMockUser(username = "Countychange", roles = {"CLIENT"})
    public void testCountyChange() throws Exception {
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);
        client.setUsername("Countychange");
        client.setFirstName("County");
        client.setLastName("Client");
        client.setEmail("County@example.com");
        client.setCounty(County.BUCURESTI);
        client.setAge(25);
        client.setPhoneNumber("0712345678");

        clientRepository.save(client);

        County[] counties = {County.CLUJ, County.TIMIS, County.CONSTANTA};

        for (County county : counties) {
            ClientDTO updateDTO = new ClientDTO();
            updateDTO.setCounty(county);

            mockMvc.perform(put("/v1/users/client/profile")
                            .header(USER_ID_HEADER, clientId.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.county", is(county.toString())));
        }
    }

    @Test
    @WithMockUser(username = "todelete", roles = {"SELLER"})
    public void testDeleteUserWorkflow() throws Exception {
        UUID sellerId = UUID.randomUUID();
        Seller seller = new Seller();
        seller.setId(sellerId);
        seller.setUsername("todelete");
        seller.setEmail("delete@seller.com");
        seller.setImageName("https://example.com/logo.png");
        seller.setDescription("To be deleted");
        seller.setCounty(County.BUCURESTI);

        sellerRepository.save(seller);
        assertTrue(sellerRepository.existsById(sellerId));

        mockMvc.perform(delete("/v1/users/seller/profile")
                        .header(USER_ID_HEADER, sellerId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertFalse(sellerRepository.existsById(sellerId));

        mockMvc.perform(get("/v1/users/seller/profile")
                        .header(USER_ID_HEADER, sellerId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("deleteUser-Topic-Exchange"),
                eq("deleteUser"),
                argThat((Object msg) -> msg instanceof DeleteUserDTO && ((DeleteUserDTO) msg).getId().equals(sellerId))
        );
    }

    @Test
    @WithMockUser(username = "deleter", roles = {"CLIENT"})
    public void testDeleteMultipleUsersRabbitMQVerification() throws Exception {
        List<UUID> clientIds = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            UUID clientId = UUID.randomUUID();
            clientIds.add(clientId);

            Client client = new Client();
            client.setId(clientId);
            client.setUsername("client" + i);
            client.setFirstName("Client" + i);
            client.setLastName("Last" + i);
            client.setEmail("client" + i + "@example.com");
            client.setCounty(County.BUCURESTI);
            client.setAge(25 + i);
            client.setPhoneNumber("071" + (2000000 + i));

            clientRepository.save(client);
        }

        for (UUID clientId : clientIds) {
            mockMvc.perform(delete("/v1/users/client/profile")
                            .header(USER_ID_HEADER, clientId.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }

        verify(rabbitTemplate, times(2)).convertAndSend(
                eq("deleteUser-Topic-Exchange"),
                eq("deleteUser"),
                (Object) org.mockito.ArgumentMatchers.any(DeleteUserDTO.class)
        );
    }

    @Test
    @WithMockUser(username = "messagecontent", roles = {"CLIENT"})
    public void testRabbitMQMessageContentForDeleteUser() throws Exception {
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);
        client.setUsername("messagecontent");
        client.setFirstName("Message");
        client.setLastName("Content");
        client.setEmail("message@example.com");
        client.setCounty(County.BUCURESTI);
        client.setAge(25);
        client.setPhoneNumber("0712345678");

        clientRepository.save(client);

        mockMvc.perform(delete("/v1/users/client/profile")
                        .header(USER_ID_HEADER, clientId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(rabbitTemplate).convertAndSend(
                eq("deleteUser-Topic-Exchange"),
                eq("deleteUser"),
                (Object) argThat(msg -> {
                    if (!(msg instanceof DeleteUserDTO)) return false;
                    DeleteUserDTO dto = (DeleteUserDTO) msg;
                    return dto.getId().equals(clientId);
                })
        );
    }
}
