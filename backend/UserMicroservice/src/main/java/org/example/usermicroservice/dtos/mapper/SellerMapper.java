package org.example.usermicroservice.dtos.mapper;

import org.example.usermicroservice.dtos.SellerDTO;
import org.example.usermicroservice.entities.Seller;

public class SellerMapper {

    public static SellerDTO toDTO(Seller seller) {
        SellerDTO dto = new SellerDTO();
        dto.setId(seller.getId());
        dto.setUsername(seller.getUsername());
        dto.setEmail(seller.getEmail());
        dto.setImageName(seller.getImageName());
        dto.setDescription(seller.getDescription());
        dto.setRegion(seller.getRegion());
        return dto;
    }

    public static Seller toEntity(SellerDTO dto) {
        Seller seller = new Seller();
        seller.setId(dto.getId());
        seller.setUsername(dto.getUsername());
        seller.setEmail(dto.getEmail());
        seller.setImageName(dto.getImageName());
        seller.setDescription(dto.getDescription());
        seller.setRegion(dto.getRegion());
        return seller;
    }

    public static void updateEntityFromDTO(SellerDTO dto, Seller seller) {
        if (dto.getEmail() != null) {
            seller.setEmail(dto.getEmail());
        }
        if (dto.getImageName() != null) {
            seller.setImageName(dto.getImageName());
        }
        if (dto.getDescription() != null) {
            seller.setDescription(dto.getDescription());
        }
        if (dto.getRegion() != null) {
            seller.setRegion(dto.getRegion());
        }
    }
}

