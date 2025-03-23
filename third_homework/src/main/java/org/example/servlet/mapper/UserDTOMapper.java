package org.example.servlet.mapper;

import org.example.model.UserEntity;
import org.example.servlet.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserDTOMapper {
    UserDTOMapper INSTANCE = Mappers.getMapper(UserDTOMapper.class);

    UserDTO mapToDTO(UserEntity user);

    UserEntity mapToEntity(UserDTO userDTO);
}