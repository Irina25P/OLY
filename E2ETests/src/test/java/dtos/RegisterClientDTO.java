package dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterClientDTO {
    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private String password;

    private String county;

    private Integer age;

    private String phoneNumber;

    private List<String> interests;
}