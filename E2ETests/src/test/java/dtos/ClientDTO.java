package dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import constants.County;
import constants.InterestType;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private County county;
    private Integer age;
    private String phoneNumber;
    private List<InterestType> interests;
}
