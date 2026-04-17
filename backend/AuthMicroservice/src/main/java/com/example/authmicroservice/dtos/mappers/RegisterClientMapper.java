package com.example.authmicroservice.dtos.mappers;

import com.example.authmicroservice.constants.Role;
import com.example.authmicroservice.dtos.RegisterClientDTO;
import com.example.authmicroservice.entities.User;

public class RegisterClientMapper {
    private RegisterClientMapper() {}

    public static User from(RegisterClientDTO dto) {
        return new User(dto.getUsername(), dto.getPassword(), Role.CLIENT);
    }
}
