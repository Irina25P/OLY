package org.example.usermicroservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.usermicroservice.entities.Region;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerDTO {
    private UUID id;
    private String username;
    private String email;
    private String imageName;
    private String description;
    private Region region;
}

