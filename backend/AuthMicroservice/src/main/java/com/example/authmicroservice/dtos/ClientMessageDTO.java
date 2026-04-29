package com.example.authmicroservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClientMessageDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String county;
    private Integer age;
    private String phoneNumber;
    private List<String> interests;
}
