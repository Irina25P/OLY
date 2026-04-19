package org.example.usermicroservice.dtos.mapper;

import org.example.usermicroservice.dtos.ClientDTO;
import org.example.usermicroservice.entities.Client;
import org.example.usermicroservice.entities.Interest;
import org.example.usermicroservice.entities.InterestType;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClientMapper {

    public static ClientDTO toDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setUsername(client.getUsername());
        dto.setFirstName(client.getFirstName());
        dto.setLastName(client.getLastName());
        dto.setEmail(client.getEmail());
        dto.setRegion(client.getRegion());
        dto.setAge(client.getAge());
        dto.setPhoneNumber(client.getPhoneNumber());
        if (client.getInterests() != null) {
            dto.setInterests(client.getInterests().stream()
                    .map(Interest::getInterestType)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public static Client toEntity(ClientDTO dto) {
        Client client = new Client();
        client.setId(dto.getId());
        client.setUsername(dto.getUsername());
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setEmail(dto.getEmail());
        client.setRegion(dto.getRegion());
        client.setAge(dto.getAge());
        client.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getInterests() != null) {
            List<Interest> interests = dto.getInterests().stream().map(interestType -> {
                Interest interest = new Interest();
                interest.setId(UUID.randomUUID());
                interest.setInterestType(interestType);
                return interest;
            }).collect(Collectors.toList());
            client.setInterests(interests);
        }
        return client;
    }

    public static void updateEntityFromDTO(ClientDTO dto, Client client) {
        if (dto.getFirstName() != null) {
            client.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            client.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            client.setEmail(dto.getEmail());
        }
        if (dto.getRegion() != null) {
            client.setRegion(dto.getRegion());
        }
        if (dto.getAge() != null) {
            client.setAge(dto.getAge());
        }
        if (dto.getPhoneNumber() != null) {
            client.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getInterests() != null) {
            client.getInterests().clear();
            dto.getInterests().forEach(interestType -> {
                Interest interest = new Interest();
                interest.setId(UUID.randomUUID());
                interest.setInterestType(interestType);
                client.getInterests().add(interest);
            });
        }
    }
}
