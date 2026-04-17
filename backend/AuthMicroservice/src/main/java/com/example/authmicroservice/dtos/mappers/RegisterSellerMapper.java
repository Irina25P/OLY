package com.example.authmicroservice.dtos.mappers;

import com.example.authmicroservice.constants.Role;
import com.example.authmicroservice.dtos.RegisterSellerDTO;
import com.example.authmicroservice.entities.User;

public class RegisterSellerMapper {
    private RegisterSellerMapper() {}

    public static User from(RegisterSellerDTO dto) {
        return new User(dto.getUsername(), dto.getPassword(), Role.SELLER);
    }
}
