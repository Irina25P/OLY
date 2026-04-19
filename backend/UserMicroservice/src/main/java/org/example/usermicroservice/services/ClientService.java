package org.example.usermicroservice.services;

import org.example.usermicroservice.dtos.ClientDTO;
import org.example.usermicroservice.dtos.DeleteUserDTO;
import org.example.usermicroservice.dtos.mapper.ClientMapper;
import org.example.usermicroservice.entities.Client;
import org.example.usermicroservice.messaging.producer.DeleteUserProducer;
import org.example.usermicroservice.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final DeleteUserProducer deleteUserProducer;

    public ClientService(ClientRepository clientRepository, DeleteUserProducer deleteUserProducer) {
        this.clientRepository = clientRepository;
        this.deleteUserProducer = deleteUserProducer;
    }

    public Client saveClientProfile(Client client) {
        return clientRepository.save(client);
    }

    public ClientDTO getClientProfile(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
        return ClientMapper.toDTO(client);
    }

    public ClientDTO updateClientProfile(UUID id, ClientDTO dto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
        ClientMapper.updateEntityFromDTO(dto, client);
        return ClientMapper.toDTO(clientRepository.save(client));
    }

    public void deleteClientProfile(UUID id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
        
        DeleteUserDTO deleteMessage = new DeleteUserDTO(id);
        deleteUserProducer.sendDeleteUserMessage(deleteMessage);
    }
}
