package com.example.authmicroservice.dtos.mappers;

import com.example.authmicroservice.dtos.AdminMessageDTO;
import com.example.authmicroservice.dtos.RegisterAdminDTO;
import com.example.authmicroservice.entities.User;

import java.util.UUID;

public class AdminMessageMapper {
    private AdminMessageMapper() {}

    public static AdminMessageDTO from(RegisterAdminDTO dto, UUID id) {
        return new AdminMessageDTO(
                id,
                dto.getUsername()
        );
    }
}

