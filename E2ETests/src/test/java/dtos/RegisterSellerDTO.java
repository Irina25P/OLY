package dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterSellerDTO {
    private String name;

    private String email;

    private String username;

    private String password;

    private String imageName;

    private String description;

    private String county;
}
