package org.example.controller.mapper;

import org.example.controller.dto.UserDTO;
import org.example.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {
    @Mapping(target = "password", ignore = true)
    UserDTO mapToDTO(UserEntity user);

    UserEntity mapToEntity(UserDTO userDTO);
}