package com.example.authmicroservice.dtos.mappers;

import com.example.authmicroservice.dtos.RegisterSellerDTO;
import com.example.authmicroservice.dtos.SellerMessageDTO;

import java.util.UUID;

public class SellerMessageMapper {
    private SellerMessageMapper() {}

    public static SellerMessageDTO from(RegisterSellerDTO dto, UUID id) {
        return new SellerMessageDTO(
                id,
                dto.getName(),
                dto.getEmail(),
                dto.getUsername(),
                dto.getLogoURL(),
                dto.getDescription(),
                dto.getRegion()
        );
    }
}
