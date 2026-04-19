package org.example.usermicroservice.dtos.mapper;

import org.example.usermicroservice.dtos.DeleteUserDTO;

public class DeleteUserMapper {

    public static DeleteUserDTO toDTO(DeleteUserDTO messageDTO) {
        DeleteUserDTO dto = new DeleteUserDTO();
        dto.setId(messageDTO.getId());
        return dto;
    }
}

