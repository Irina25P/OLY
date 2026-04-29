package org.example.usermicroservice.messaging.consumer;

import org.example.usermicroservice.dtos.ClientDTO;
import org.example.usermicroservice.entities.Client;
import org.example.usermicroservice.entities.Interest;
import org.example.usermicroservice.services.ClientService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ClientMessageConsumer {

    private final ClientService clientService;
    private ObjectMapper objectMapper;

    public ClientMessageConsumer(ClientService clientService, ObjectMapper objectMapper) {
        this.clientService = clientService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.user}.${rabbitmq.sync.unique.name}.${rabbitmq.sync.routingkey.client}")
    public void consumeClientMessage(String message) {
        try {
            ClientDTO clientDTO = objectMapper.readValue(message, ClientDTO.class);
            Client client = new Client();
            client.setId(clientDTO.getId());
            client.setUsername(clientDTO.getUsername());
            client.setFirstName(clientDTO.getFirstName());
            client.setLastName(clientDTO.getLastName());
            client.setEmail(clientDTO.getEmail());
            client.setCounty(clientDTO.getCounty());
            client.setAge(clientDTO.getAge());
            client.setPhoneNumber(clientDTO.getPhoneNumber());
            
            if (clientDTO.getInterests() != null && !clientDTO.getInterests().isEmpty()) {
                List<Interest> interests = new ArrayList<>();
                clientDTO.getInterests().forEach(interestType -> {
                    Interest interest = new Interest();
                    interest.setId(UUID.randomUUID());
                    interest.setInterestType(interestType);
                    interests.add(interest);
                });
                client.setInterests(interests);
            }
            
            clientService.saveClientProfile(client);
            System.out.println("Client saved from RabbitMQ: " + clientDTO.getId());
        } catch (Exception e) {
            System.err.println("Error processing client message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

