package org.example.service;

import liquibase.exception.LiquibaseException;
import org.example.annotation.Loggable;
import org.example.model.UserEntity;
import org.example.repository.UserRepository;
import org.example.servlet.dto.UserDTO;
import org.example.servlet.mapper.UserDTOMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Loggable
public class UserService {
    UserRepository userRepository;
    UserDTOMapper userDTOMapper = UserDTOMapper.INSTANCE;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO add(UserDTO userDTO) throws SQLException, LiquibaseException {
        return userDTOMapper.mapToDTO(userRepository.add(userDTOMapper.mapToEntity(userDTO)));
    }

    public UserDTO findById(int id) throws SQLException, LiquibaseException {
        return userDTOMapper.mapToDTO(userRepository.findById(id));
    }

    public List<UserDTO> findAll() throws SQLException, LiquibaseException {
        List<UserEntity> userEntities = userRepository.findAll();
        List<UserDTO> userDTOS = new ArrayList<>();

        for (UserEntity user : userEntities) {
            userDTOS.add(userDTOMapper.mapToDTO(user));
        }

        return userDTOS;
    }

    public UserDTO update(UserDTO userDTO, int id) throws SQLException, LiquibaseException {
        UserDTO dto = new UserDTO.UserBuilder(userDTO.getEmail(), userDTO.getPassword(), userDTO.getName()).
                id(id).role(userDTO.getRole()).isBlocked(userDTO.getBlocked()).build();
        userRepository.update(userDTOMapper.mapToEntity(dto));

        return userDTOMapper.mapToDTO(userRepository.findById(id));
    }

    public boolean delete(int id) throws SQLException, LiquibaseException {
        UserEntity user = userRepository.findById(id);

        return userRepository.delete(user);
    }

    public UserDTO findUserWithEmailAndPassword(String email, String password) throws SQLException, LiquibaseException {
        return userDTOMapper.mapToDTO(userRepository.findUserWithEmailAndPassword(email, password));
    }
}