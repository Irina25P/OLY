package org.example.usermicroservice.dtos.mapper;

import org.example.usermicroservice.dtos.AdminDTO;
import org.example.usermicroservice.entities.Admin;

public class AdminMapper {

    public static AdminDTO toDTO(Admin admin) {
        AdminDTO dto = new AdminDTO();
        dto.setId(admin.getId());
        dto.setUsername(admin.getUsername());
        return dto;
    }

    public static Admin toEntity(AdminDTO dto) {
        Admin admin = new Admin();
        admin.setId(dto.getId());
        admin.setUsername(dto.getUsername());
        return admin;
    }

    public static void updateEntityFromDTO(AdminDTO dto, Admin admin) {
        admin.setUsername(dto.getUsername());
    }
}

