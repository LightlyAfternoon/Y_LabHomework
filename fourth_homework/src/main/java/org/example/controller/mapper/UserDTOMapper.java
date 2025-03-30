package org.example.controller.mapper;

import org.example.controller.dto.UserDTO;
import org.example.model.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {
    UserDTO mapToDTO(UserEntity user);

    UserEntity mapToEntity(UserDTO userDTO);
}