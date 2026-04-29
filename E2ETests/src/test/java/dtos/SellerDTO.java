package dtos;

import constants.County;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerDTO {
    private UUID id;
    private String name;
    private String username;
    private String email;
    private String imageName;
    private String description;
    private County county;
}

