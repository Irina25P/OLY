package com.example.authmicroservice.dtos.mappers;

import com.example.authmicroservice.constants.Role;
import com.example.authmicroservice.dtos.RegisterAdminDTO;
import com.example.authmicroservice.entities.User;

public class RegisterAdminMapper {
    private RegisterAdminMapper() {}

    public static User from(RegisterAdminDTO dto) {
        return new User(dto.getUsername(), dto.getPassword(), Role.ADMIN);
    }
}
