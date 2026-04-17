package com.example.authmicroservice.dtos.mappers;

import com.example.authmicroservice.dtos.ClientMessageDTO;
import com.example.authmicroservice.dtos.RegisterClientDTO;

import java.util.UUID;

public class ClientMessageMapper {
    private ClientMessageMapper() {}

    public static ClientMessageDTO from(RegisterClientDTO dto, UUID id) {
        return new ClientMessageDTO(
                id,
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getUsername(),
                dto.getRegion(),
                dto.getAge(),
                dto.getPhoneNumber(),
                dto.getInterests()
        );
    }
}

