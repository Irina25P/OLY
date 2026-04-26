package org.example.usermicroservice.messaging.consumer;

import org.example.usermicroservice.dtos.ClientDTO;
import org.example.usermicroservice.entities.Client;
import org.example.usermicroservice.entities.Interest;
import org.example.usermicroservice.services.ClientService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ClientMessageConsumer {

    private final ClientService clientService;

    public ClientMessageConsumer(ClientService clientService) {
        this.clientService = clientService;
    }

    @RabbitListener(queues = "${rabbitmq.sync.exchange.adduser}.${rabbitmq.sync.unique.name}.${rabbitmq.sync.routingkey.client}")
    public void consumeClientMessage(ClientDTO message) {
        try {
            Client client = new Client();
            client.setId(message.getId());
            client.setUsername(message.getUsername());
            client.setFirstName(message.getFirstName());
            client.setLastName(message.getLastName());
            client.setEmail(message.getEmail());
            client.setRegion(message.getRegion());
            client.setAge(message.getAge());
            client.setPhoneNumber(message.getPhoneNumber());
            
            if (message.getInterests() != null && !message.getInterests().isEmpty()) {
                List<Interest> interests = new ArrayList<>();
                message.getInterests().forEach(interestType -> {
                    Interest interest = new Interest();
                    interest.setId(UUID.randomUUID());
                    interest.setInterestType(interestType);
                    interests.add(interest);
                });
                client.setInterests(interests);
            }
            
            clientService.saveClientProfile(client);
            System.out.println("Client saved from RabbitMQ: " + message.getId());
        } catch (Exception e) {
            System.err.println("Error processing client message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

