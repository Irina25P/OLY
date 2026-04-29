package com.example.authmicroservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SellerMessageDTO {
    private UUID id;
    private String name;
    private String email;
    private String username;
    private String imageName;
    private String description;
    private String county;
}
